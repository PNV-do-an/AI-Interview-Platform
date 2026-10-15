package com.aiinterview.platform.service.ai;

import com.aiinterview.platform.model.entity.JobDescription;
import com.aiinterview.platform.model.entity.User;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Rule-based CV analyzer and cover letter generator (Vietnamese output).
 */
@Component
public class CvAiEngine {

    private static final List<String> SKILL_DICTIONARY = List.of(
            "java", "spring boot", "spring", "python", "javascript", "typescript", "react", "angular",
            "vue", "node.js", "nodejs", "express", "sql", "mysql", "postgresql", "mongodb", "redis",
            "docker", "kubernetes", "aws", "azure", "gcp", "git", "html", "css", "tailwind",
            "php", "laravel", "wordpress", "excel", "power bi", "tableau", "machine learning",
            "deep learning", "ai", "tensorflow", "pytorch", "c++", "c#", ".net", "go", "rust",
            "swift", "kotlin", "flutter", "dart", "android", "ios", "figma", "ui/ux",
            "jira", "agile", "scrum", "ci/cd", "jenkins", "linux", "bash", "rest api", "graphql",
            "microservices", "kafka", "rabbitmq", "elasticsearch", "solidity", "blockchain"
    );

    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,}");
    private static final Pattern PHONE_PATTERN = Pattern.compile("(\\+?84|0)\\d{9,10}");

    /** Detects skills, scores the CV and returns improvement suggestions. */
    public CvAnalysis analyze(String cvText) {
        String lower = cvText.toLowerCase();

        List<String> skills = SKILL_DICTIONARY.stream()
                .filter(lower::contains)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        List<String> suggestions = new ArrayList<>();
        int score = 0;

        if (EMAIL_PATTERN.matcher(cvText).find()) score += 10;
        else suggestions.add("Thêm email liên hệ ở phần đầu CV.");

        if (PHONE_PATTERN.matcher(cvText).find()) score += 10;
        else suggestions.add("Thêm số điện thoại liên hệ.");

        if (containsAny(lower, "kinh nghiệm", "experience", "work history")) score += 20;
        else suggestions.add("Bổ sung mục KINH NGHIỆM LÀM VIỆC với các dự án/công việc cụ thể.");

        if (containsAny(lower, "học vấn", "education", "đại học", "university", "cử nhân", "bachelor", "thạc sĩ", "master")) score += 15;
        else suggestions.add("Bổ sung mục HỌC VẤN (trường, chuyên ngành, thời gian).");

        if (containsAny(lower, "kỹ năng", "skills")) score += 15;
        else suggestions.add("Thêm mục KỸ NĂNG liệt kê rõ kỹ năng chuyên môn và kỹ năng mềm.");

        if (!skills.isEmpty()) score += 15;
        else suggestions.add("Chưa phát hiện kỹ năng chuyên môn nào phổ biến — hãy liệt kê công nghệ bạn sử dụng.");

        if (containsAny(lower, "mục tiêu", "objective", "tóm tắt", "summary", "profile")) score += 10;
        else suggestions.add("Thêm 2-3 dòng TÓM TẮT / MỤC TIÊU NGHỀ NGHIỆP ở đầu CV.");

        int wordCount = lower.trim().isEmpty() ? 0 : lower.trim().split("\\s+").length;
        if (wordCount >= 200) score += 5;
        else suggestions.add("CV còn quá ngắn (" + wordCount + " từ) — mô tả chi tiết hơn vai trò và thành tựu.");

        if (containsAny(lower, "thành tựu", "achievement", "kết quả", "%", "tăng", "giảm")) score += 10;
        else suggestions.add("Nêu THÀNH TỰU định lượng (VD: tăng hiệu suất 30%, giảm chi phí 20%).");

        return new CvAnalysis(skills, Math.min(100, score), suggestions);
    }

    /** Matches a cover letter to a job's requirements. */
    public List<String> matchJob(List<String> cvSkills, JobDescription job) {
        if (cvSkills == null || job == null || job.getRequiredSkills() == null) {
            return List.of();
        }
        List<String> required = Arrays.stream(job.getRequiredSkills().split("[,;\\n]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .toList();
        Set<String> cvLower = cvSkills.stream().map(String::toLowerCase).collect(Collectors.toSet());
        return required.stream()
                .filter(r -> cvLower.stream().anyMatch(r::contains))
                .distinct()
                .collect(Collectors.toList());
    }

    /** Generates a Vietnamese cover letter from candidate profile, CV skills and target job. */
    public String generateCoverLetter(User candidate, List<String> skills, Integer experienceYears,
                                      JobDescription job, List<String> matchedSkills) {
        String name = candidate.getFullName();
        String email = candidate.getEmail() != null ? candidate.getEmail() : "";
        String position = job != null && job.getPosition() != null && !job.getPosition().isBlank()
                ? job.getPosition()
                : job != null ? job.getTitle() : "vị trí ứng tuyển";
        String company = job != null ? job.getCompanyName() : "Quý công ty";

        String topSkills = skills.isEmpty()
                ? "khả năng học hỏi nhanh và tinh thần trách nhiệm cao"
                : "các kỹ năng " + String.join(", ", skills.subList(0, Math.min(5, skills.size())));

        String expPhrase = experienceYears != null && experienceYears > 0
                ? "Với " + experienceYears + " năm kinh nghiệm, "
                : "Là một ứng viên nhiệt huyết, ";

        String matchPhrase = matchedSkills == null || matchedSkills.isEmpty()
                ? ""
                : "Tôi nhận thấy yêu cầu của vị trí về "
                  + String.join(", ", matchedSkills)
                  + " hoàn toàn phù hợp với hành trình nghề nghiệp của mình. ";

        StringBuilder sb = new StringBuilder();
        sb.append("Kính gửi Ban Tuyển dụng ").append(company).append(",\n\n");
        sb.append("Tôi tên là ").append(name)
          .append(", viết thư này để bày tỏ mong muốn ứng tuyển vị trí ")
          .append(position).append(" tại ").append(company).append(".\n\n");
        sb.append(expPhrase)
          .append("tôi tự tin mang đến cho đội ngũ của quý công ty ")
          .append(topSkills).append(". ");
        sb.append(matchPhrase);
        sb.append("Trong các dự án trước đây, tôi luôn chủ động học hỏi, làm việc có hệ thống và đặt chất lượng kết quả lên hàng đầu.\n\n");
        sb.append("Tôi tin rằng với nền tảng và tinh thần cống hiến của mình, tôi sẽ nhanh chóng đóng góp giá trị thực tế cho ")
          .append(company).append(". Rất mong được trao đổi trực tiếp trong buổi phỏng vấn.\n\n");
        sb.append("Trân trọng,\n");
        sb.append(name).append("\n");
        sb.append(email);
        return sb.toString();
    }

    private boolean containsAny(String text, String... keywords) {
        for (String k : keywords) {
            if (text.contains(k)) return true;
        }
        return false;
    }

    public record CvAnalysis(List<String> skills, int score, List<String> suggestions) {
    }
}
