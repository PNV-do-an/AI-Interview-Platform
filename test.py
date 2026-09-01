import sys
if sys.platform == "win32":
    try:
        sys.stdout.reconfigure(encoding='utf-8')
        sys.stderr.reconfigure(encoding='utf-8')
    except Exception:
        pass

import os
import numpy as np

def test_devices():
    print("=== [1] KIEM TRA THIET BI AM THANH ===")
    import sounddevice as sd
    devices = sd.query_devices()
    print(devices)
    default_in = sd.query_devices(kind='input')
    default_out = sd.query_devices(kind='output')
    print(f"\nMicro mac dinh: {default_in['name']}")
    print(f"Loa mac dinh: {default_out['name']}")
    print("[OK] Thiet bi am thanh: SAN SANG\n")

def test_gemini():
    print("=== [2] KIEM TRA GEMINI API ===")
    from gemini_agent import GeminiAgent
    try:
        agent = GeminiAgent()
        prompt = "Chào bạn! Hãy giới thiệu ngắn gọn trong 1 câu."
        print(f"[User]: {prompt}")
        response = agent.ask(prompt)
        print(f"[Gemini]: {response}")
        print("[OK] Gemini API: HOAT DONG TOT\n")
    except Exception as e:
        print(f"[ERROR] Loi Gemini API: {e}\n")

def test_tts():
    print("=== [3] KIEM TRA VIENEU-TTS-V3-TURBO ===")
    from tts_engine import VieNeuTTS
    try:
        tts = VieNeuTTS()
        sample_text = "Xin chào, đây là thử nghiệm giọng đọc VieNeu TTS."
        print(f"[TTS] Đang đọc: {sample_text}")
        tts.speak(sample_text, play_audio=True)
        print("[OK] VieNeu TTS: HOAT DONG TOT\n")
    except Exception as e:
        print(f"[ERROR] Loi VieNeu TTS: {e}\n")

def test_stt():
    print("=== [4] KIEM TRA PHOWHISPER-TINY ===")
    from stt_engine import PhoWhisperSTT
    try:
        stt = PhoWhisperSTT()
        # Tạo âm thanh giả lập 1 giây yên lặng để kiểm thử nạp model và pipeline
        dummy_audio = np.zeros(16000, dtype=np.float32)
        transcription = stt.transcribe(dummy_audio)
        print(f"[STT] Transcribe dummy audio test: '{transcription}'")
        print("[OK] PhoWhisper-tiny: HOAT DONG TOT\n")
    except Exception as e:
        print(f"[ERROR] Loi PhoWhisper-tiny: {e}\n")

if __name__ == "__main__":
    print("CHỌN MODULE KIỂM THỬ:")
    print("1. Kiểm tra Thiết bị Âm thanh (Mic/Loa)")
    print("2. Kiểm tra Gemini API")
    print("3. Kiểm tra VieNeu TTS")
    print("4. Kiểm tra PhoWhisper STT")
    print("5. Chạy tất cả kiểm tra")
    
    choice = input("Nhập lựa chọn (1-5, mặc định là 5): ").strip()
    if choice == "1":
        test_devices()
    elif choice == "2":
        test_gemini()
    elif choice == "3":
        test_tts()
    elif choice == "4":
        test_stt()
    else:
        test_devices()
        test_gemini()
        test_tts()
        test_stt()
