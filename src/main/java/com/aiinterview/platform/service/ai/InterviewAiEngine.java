package com.aiinterview.platform.service.ai;

import com.aiinterview.platform.model.entity.JobDescription;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Rule-based interview engine: generates questions from a job's skills and
 * scores free-text answers by keyword coverage, detail level and structure.
 */
@Component
public class InterviewAiEngine {

    private static final List<String> BEHAVIORAL_QUESTIONS = List.of(
            "Hãy giới thiệu ngắn gọn về bản thân và kinh nghiệm liên quan đến vị trí này.",
            "Kể về một dự án khó bạn từng gặp. Bạn đã giải quyết nó như thế nào?",
            "Bạn làm việc nhóm hay làm việc độc lập hiệu quả hơn? Cho ví dụ cụ thể.",
            "Khi deadline gấp và yêu cầu thay đổi liên tục, bạn ưu tiên xử lý thế nào?",
            "Điểm mạnh và điểm cần cải thiện lớn nhất của bạn trong công việc là gì?",
            "Tại sao bạn muốn làm việc tại công ty này và vị trí này?"
    );

    private static final List<String> KNOWLEDGE_TEMPLATES = List.of(
            "Hãy giải thích các khái niệm cốt lõi của {topic} mà bạn nắm vững nhất.",
            "So sánh ưu và nhược điểm giữa các công nghệ/phương pháp phổ biến trong {topic}.",
            "Trong {topic}, bạn thường xử lý lỗi (debug) và tối ưu hiệu năng như thế nào?",
            "Những best practices quan trọng nhất khi làm việc với {topic} là gì?",
            "Mô tả quy trình xây dựng một tính năng hoàn chỉnh bằng {topic} từ đầu đến cuối.",
            "Những câu hỏi phỏng vấn phổ biến về {topic} và cách bạn trả lời chúng?"
    );

    /** Generates questions for a MOCK interview based on the job's required skills. */
    public List<String> generateMockQuestions(JobDescription job, int count) {
        List<String> questions = new ArrayList<>();
        List<String> skills = parseSkills(job.getRequiredSkills());

        for (String skill : skills) {
            if (questions.size() >= count) break;
            questions.add("Hãy mô tả kinh nghiệm thực tế của bạn với " + skill + " — dự án nào đã dùng và vai trò của bạn?");
            if (questions.size() < count) {
                questions.add("Một vấn đề khó thường gặp khi làm việc với " + skill + " là gì và cách bạn giải quyết?");
            }
        }
        for (String behavioral : BEHAVIORAL_QUESTIONS) {
            if (questions.size() >= count) break;
            questions.add(behavioral);
        }
        return questions.subList(0, Math.min(count, questions.size()));
    }

    /** Generates questions for a KNOWLEDGE practice session on a topic. */
    public List<String> generateKnowledgeQuestions(String topic, int count) {
        List<String> questions = new ArrayList<>();
        String t = topic.trim();
        for (String template : KNOWLEDGE_TEMPLATES) {
            if (questions.size() >= count) break;
            questions.add(template.replace("{topic}", t));
        }
        return questions;
    }

    /** Extracts scoring keywords from job data for MOCK mode. */
    public Set<String> extractKeywords(JobDescription job, String question) {
        Set<String> keywords = new LinkedHashSet<>();
        keywords.addAll(parseSkills(job.getRequiredSkills()).stream()
                .map(String::toLowerCase).collect(Collectors.toList()));
        addLongWords(keywords, job.getTitle());
        addLongWords(keywords, job.getRequirements());
        addLongWords(keywords, job.getPosition());
        addLongWords(keywords, question);
        return keywords;
    }

    /** Extracts scoring keywords from topic for KNOWLEDGE mode. */
    public Set<String> extractKeywords(String topic, String question) {
        Set<String> keywords = new LinkedHashSet<>();
        addLongWords(keywords, topic);
        addLongWords(keywords, question);
        return keywords;
    }

    public ScoreResult evaluate(String answerText, Set<String> keywords) {
        String normalized = answerText.toLowerCase();
        Set<String> matched = new LinkedHashSet<>();
        Set<String> missing = new LinkedHashSet<>();
        for (String k : keywords) {
            if (normalized.contains(k.toLowerCase())) {
                matched.add(k);
            } else {
                missing.add(k);
            }
        }

        int wordCount = normalized.trim().isEmpty() ? 0 : normalized.trim().split("\\s+").length;

        // Keyword coverage: up to 60 points
        double coverage = keywords.isEmpty() ? 0.5 : (double) matched.size() / keywords.size();
        int keywordPoints = (int) Math.round(coverage * 60);

        // Detail level: full 30 points at >= 60 words, scaled below
        int detailPoints = Math.min(30, wordCount * 30 / 60);

        // Structure bonus: concrete example markers
        boolean hasExample = normalized.contains("ví dụ") || normalized.contains("vd:")
                || normalized.contains("dự án") || normalized.contains("project");
        int bonus = hasExample ? 10 : 0;

        int score = Math.min(100, keywordPoints + detailPoints + bonus);
        return new ScoreResult(score, buildFeedback(wordCount, matched, missing, hasExample));
    }

    private String buildFeedback(int wordCount, Set<String> matched, Set<String> missing, boolean hasExample) {
        StringBuilder fb = new StringBuilder();
        if (wordCount < 20) {
            fb.append("Câu trả lời còn ngắn (").append(wordCount)
              .append(" từ) — cần trình bày chi tiết hơn, khoảng 60 từ trở lên. ");
        } else {
            fb.append("Mức độ chi tiết tốt (").append(wordCount).append(" từ). ");
        }
        if (!matched.isEmpty()) {
            fb.append("Điểm mạnh: đề cập ").append(String.join(", ", matched)).append(". ");
        }
        if (!missing.isEmpty()) {
            List<String> topMissing = missing.stream().limit(5).collect(Collectors.toList());
            fb.append("Nên bổ sung: ").append(String.join(", ", topMissing)).append(". ");
        }
        fb.append(hasExample
                ? "Có ví dụ minh họa cụ thể — điểm cộng."
                : "Nên thêm ví dụ thực tế (dự án, tình huống) để câu trả lời thuyết phục hơn.");
        return fb.toString();
    }

    private List<String> parseSkills(String requiredSkills) {
        if (requiredSkills == null || requiredSkills.isBlank()) {
            return List.of();
        }
        return Arrays.stream(requiredSkills.split("[,;\\n]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    private void addLongWords(Set<String> keywords, String text) {
        if (text == null) return;
        for (String w : text.toLowerCase().split("[^a-z0-9à-ỹ+#.]+")) {
            if (w.length() >= 4) {
                keywords.add(w);
            }
        }
    }

    public record ScoreResult(int score, String feedback) {
    }
}
