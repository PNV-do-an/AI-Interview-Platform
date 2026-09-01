import sys
if sys.platform == "win32":
    try:
        sys.stdout.reconfigure(encoding='utf-8')
        sys.stderr.reconfigure(encoding='utf-8')
    except Exception:
        pass

import time
import numpy as np
import sounddevice as sd
import soundfile as sf
import tempfile
import os

class AudioRecorder:
    def __init__(self, sample_rate: int = 16000, channels: int = 1):
        """
        Khởi tạo module thu âm.
        PhoWhisper yêu cầu tần số lấy mẫu chuẩn 16kHz, mono.
        """
        self.sample_rate = sample_rate
        self.channels = channels

    def record_seconds(self, duration: float = 5.0) -> np.ndarray:
        """
        Thu âm trong khoảng thời gian cố định (giây).
        """
        print(f"[REC] Đang thu âm trong {duration} giây...")
        recording = sd.rec(
            int(duration * self.sample_rate),
            samplerate=self.sample_rate,
            channels=self.channels,
            dtype='float32'
        )
        sd.wait()
        print("[REC] Thu âm hoàn tất!")
        return np.squeeze(recording)

    def record_interactive(self) -> np.ndarray:
        """
        Thu âm tương tác: Bấm Enter để bắt đầu, bấm Enter để kết thúc.
        """
        input("👉 Nhấn [Enter] để BẮT ĐẦU nói...")
        
        audio_chunks = []
        is_recording = True
        
        def callback(indata, frames, time_info, status):
            if status:
                print(status)
            if is_recording:
                audio_chunks.append(indata.copy())

        stream = sd.InputStream(
            samplerate=self.sample_rate,
            channels=self.channels,
            dtype='float32',
            callback=callback
        )

        with stream:
            print("🎙️ Đang lắng nghe... Nhấn [Enter] lần nữa để DỪNG:")
            input()
            is_recording = False

        if not audio_chunks:
            return np.array([], dtype=np.float32)

        audio_data = np.concatenate(audio_chunks, axis=0)
        print(f"[REC] Thu âm hoàn tất ({len(audio_data) / self.sample_rate:.2f}s).")
        return np.squeeze(audio_data)

    def save_temp_wav(self, audio_data: np.ndarray) -> str:
        """
        Lưu dữ liệu audio numpy ra file .wav tạm thời.
        """
        temp_file = tempfile.NamedTemporaryFile(suffix=".wav", delete=False)
        sf.write(temp_file.name, audio_data, self.sample_rate)
        return temp_file.name
