import sys
if sys.platform == "win32":
    try:
        sys.stdout.reconfigure(encoding='utf-8')
        sys.stderr.reconfigure(encoding='utf-8')
    except Exception:
        pass

import os
from audio_recorder import AudioRecorder
from stt_engine import PhoWhisperSTT
from gemini_agent import GeminiAgent
from tts_engine import VieNeuTTS

def main():
    print("=" * 60)
    print("   VIETNAMESE VOICE AI ASSISTANT (WITH EMOTIONS)")
    print("   STT: PhoWhisper | LLM: Gemini API | TTS: VieNeu-TTS-v3-Turbo")
    print("=" * 60)

    # 1. Khởi tạo các module
    try:
        recorder = AudioRecorder(sample_rate=16000)
        stt = PhoWhisperSTT()
        agent = GeminiAgent()
        tts = VieNeuTTS()
    except Exception as e:
        print(f"\n[ERROR] Lỗi khởi tạo hệ thống: {e}")
        sys.exit(1)

    print("\n[READY] Tất cả mô hình đã sẵn sàng!")
    print("------------------------------------------------------------")
    print("HƯỚNG DẪN SỬ DỤNG:")
    print("  1. Nhấn [Enter] để bắt đầu thu âm.")
    print("  2. Nói câu hỏi/yêu cầu của bạn bằng Tiếng Việt.")
    print("  3. Nhấn [Enter] lần nữa để gửi và nhận phản hồi.")
    print("  4. Gõ 'q' hoặc 'exit' rồi nhấn Enter để thoát.")
    print("------------------------------------------------------------\n")

    # Phát lời chào mở đầu kèm emotion cue
    greeting = "Xin chào bạn! [cười] Tôi là trợ lý AI. Hôm nay tôi có thể giúp gì cho bạn nào?"
    print(f"[AI]: {greeting}")
    tts.speak(greeting)

    while True:
        try:
            print("\n" + "-" * 50)
            user_choice = input("👉 Nhấn [Enter] để NÓI (hoặc gõ 'q' để THOÁT): ").strip().lower()
            if user_choice in ['q', 'exit', 'quit']:
                farewell = "Tạm biệt bạn nhé! [cười] Hẹn gặp lại bạn sau!"
                print(f"[AI]: {farewell}")
                tts.speak(farewell)
                break

            # Bắt đầu thu âm
            audio_data = recorder.record_interactive()
            if len(audio_data) == 0:
                print("[WARN] Không ghi nhận được âm thanh. Vui lòng thử lại.")
                continue

            # Bước 1: STT với PhoWhisper
            print(f"[STT] Đang nhận diện giọng nói ({stt.model_id})...")
            user_text = stt.transcribe(audio_data)
            print(f"[USER]: \"{user_text}\"")

            if not user_text.strip():
                print("[WARN] Không nghe rõ nội dung nói. Vui lòng nói to và rõ hơn.")
                continue

            # Bước 2: Xử lý bằng Gemini API (tự động chèn emotion cues)
            print("[Gemini] Đang suy nghĩ câu trả lời cảm xúc...")
            ai_response = agent.ask(user_text)
            print(f"[AI]: \"{ai_response}\"")

            # Bước 3: Tổng hợp giọng nói TTS với VieNeu-TTS-v3-Turbo
            print("[TTS] Đang chuyển câu trả lời thành giọng nói có cảm xúc...")
            tts.speak(ai_response)

        except KeyboardInterrupt:
            print("\n[INFO] Đã dừng chương trình.")
            break
        except Exception as e:
            print(f"\n[ERROR] Đã xảy ra lỗi: {e}")

if __name__ == "__main__":
    main()
