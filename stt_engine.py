import sys
if sys.platform == "win32":
    try:
        sys.stdout.reconfigure(encoding='utf-8')
        sys.stderr.reconfigure(encoding='utf-8')
    except Exception:
        pass

import os
import warnings
import logging

# Tắt các cảnh báo deprecation và log dư thừa từ transformers / huggingface
warnings.filterwarnings("ignore")
logging.getLogger("transformers").setLevel(logging.ERROR)
logging.getLogger("huggingface_hub").setLevel(logging.ERROR)

from dotenv import load_dotenv
import torch
from transformers import pipeline
import numpy as np

load_dotenv()

class PhoWhisperSTT:
    def __init__(self, model_id: str = None, device: str = None):
        """
        Khởi tạo PhoWhisper STT engine.
        Mặc định sử dụng model được cấu hình trong .env (STT_MODEL) hoặc 'vinai/PhoWhisper-small'.
        """
        if model_id is None:
            model_id = os.getenv("STT_MODEL", "vinai/PhoWhisper-small")

        if device is None:
            device = "cuda:0" if torch.cuda.is_available() else "cpu"
        
        self.model_id = model_id
        self.device = device
        print(f"[STT] Đang nạp model [{self.model_id}] trên thiết bị: {self.device}...")
        
        self.pipe = pipeline(
            "automatic-speech-recognition",
            model=self.model_id,
            device=self.device,
            generate_kwargs={"task": "transcribe", "language": "vi"}
        )
        print(f"[STT] PhoWhisper STT ({self.model_id}) đã sẵn sàng!")

    def transcribe(self, audio_input) -> str:
        """
        Chuyển giọng nói thành văn bản.
        audio_input có thể là đường dẫn file .wav hoặc numpy array (16kHz float32).
        """
        if isinstance(audio_input, np.ndarray):
            audio_input = {
                "raw": audio_input,
                "sampling_rate": 16000
            }
        
        result = self.pipe(audio_input)
        text = result.get("text", "").strip()
        return text
