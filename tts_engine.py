import sys
if sys.platform == "win32":
    try:
        sys.stdout.reconfigure(encoding='utf-8')
        sys.stderr.reconfigure(encoding='utf-8')
    except Exception:
        pass

import os
import tempfile
import soundfile as sf
import sounddevice as sd
import numpy as np

class VieNeuTTS:
    def __init__(self, backend: str = None, voice: str = None):
        """
        Khởi tạo VieNeu-TTS-v3-Turbo Engine.
        """
        print("[TTS] Đang nạp mô hình giọng đọc VieNeu-TTS-v3-Turbo...")
        from vieneu import Vieneu
        
        # Vieneu hỗ trợ backend onnx (nhanh trên CPU) hoặc torch
        if backend:
            self.tts = Vieneu(backend=backend)
        else:
            self.tts = Vieneu()
            
        self.voice = voice
        print("[TTS] VieNeu-TTS đã sẵn sàng!")

    def speak(self, text: str, play_audio: bool = True) -> str:
        """
        Tổng hợp giọng nói từ văn bản và phát ra loa.
        Trả về đường dẫn file âm thanh đã lưu.
        """
        if not text or not text.strip():
            return ""
        
        print(f"[TTS] Trợ lý AI đang đọc: \"{text}\"")
        
        # Sinh âm thanh từ VieNeu-TTS
        if self.voice:
            audio_output = self.tts.infer(text=text, voice=self.voice)
        else:
            audio_output = self.tts.infer(text=text)
            
        temp_wav = tempfile.NamedTemporaryFile(suffix=".wav", delete=False)
        temp_wav_path = temp_wav.name
        temp_wav.close()

        # Lưu audio ra file wav
        self.tts.save(audio_output, temp_wav_path)
        
        # Phát âm thanh nếu được yêu cầu
        if play_audio:
            self.play_wav(temp_wav_path)
            
        return temp_wav_path

    @staticmethod
    def play_wav(wav_path: str):
        """
        Phát file wav ra loa bằng sounddevice.
        """
        data, fs = sf.read(wav_path, dtype='float32')
        sd.play(data, fs)
        sd.wait()
