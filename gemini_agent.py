import sys
if sys.platform == "win32":
    try:
        sys.stdout.reconfigure(encoding='utf-8')
        sys.stderr.reconfigure(encoding='utf-8')
    except Exception:
        pass

import os
from dotenv import load_dotenv
from google import genai
from google.genai import types

load_dotenv()

class GeminiAgent:
    def __init__(self, model_name: str = None, api_key: str = None):
        """
        Khởi tạo Gemini Agent hỗ trợ đàm thoại đa lượt với khả năng
        tự động sinh emotion cues ([cười], [thở dài], [hắng giọng]) cho VieNeu-TTS.
        """
        if api_key is None:
            api_key = os.getenv("GEMINI_API_KEY")
            
        if not api_key or api_key == "your_gemini_api_key_here":
            raise ValueError(
                "Chưa cấu hình GEMINI_API_KEY! "
                "Vui lòng tạo file .env và điền API Key từ https://aistudio.google.com/."
            )

        if model_name is None:
            model_name = os.getenv("GEMINI_MODEL", "gemini-2.5-flash")

        self.model_name = model_name
        self.client = genai.Client(api_key=api_key)
        
        # System instruction tối ưu cho trợ lý giọng nói có cảm xúc (VieNeu-TTS-v3-Turbo)
        self.system_instruction = (
            "Bạn là một trợ lý ảo thông minh, sinh động, giàu cảm xúc và giao tiếp hoàn toàn bằng Tiếng Việt.\n"
            "Câu trả lời của bạn sẽ được mô hình VieNeu-TTS-v3-Turbo chuyển thành giọng nói để phát trực tiếp cho người dùng nghe.\n\n"
            "QUY TẮC NHẤN NHÁ VÀ CẢM XÚC CHO GIỌNG ĐỌC:\n"
            "Mô hình giọng nói hỗ trợ các thẻ cảm xúc ngữ điệu đặt trong ngoặc vuông: [cười], [thở dài], [hắng giọng].\n"
            "Hãy tự động và linh hoạt lồng ghép các thẻ này vào câu văn một cách tự nhiên nhất để giọng đọc trôi chảy, sinh động như người thật:\n"
            "- Dùng [cười] khi chào hỏi thân thiện, bày tỏ niềm vui, khi khen ngợi hoặc nói đùa hóm hỉnh (ví dụ: 'Chào bạn nhé! [cười] Hôm nay bạn thế nào?').\n"
            "- Dùng [hắng giọng] khi bắt đầu chuyển chủ đề, chuẩn bị giải thích một vấn đề hay gây sự chú ý (ví dụ: '[hắng giọng] Để mình nói cho bạn nghe nhé...').\n"
            "- Dùng [thở dài] khi bày tỏ sự đồng cảm, chia buồn, tiếc nuối hoặc gặp tình huống éo le.\n\n"
            "QUY TẮC PHẢN HỒI:\n"
            "- Trả lời ngắn gọn, cô đọng, tự nhiên như đàm thoại trực tiếp ngoài đời.\n"
            "- Không lạm dụng quá nhiều thẻ trong một câu ngắn (chỉ cần 1-2 thẻ ở vị trí thích hợp nhất).\n"
            "- Tuyệt đối không dùng bảng biểu markdown, code block dài hoặc danh sách gạch đầu dòng phức tạp."
        )

        self.chat = self.client.chats.create(
            model=self.model_name,
            config=types.GenerateContentConfig(
                system_instruction=self.system_instruction,
                temperature=0.7,
            )
        )
        print(f"[Gemini] Đã kết nối thành công (Model: {self.model_name}) với Emotional Cues!")

    def ask(self, user_text: str) -> str:
        """
        Gửi tin nhắn của người dùng và nhận câu trả lời từ Gemini có kèm emotion cues.
        """
        if not user_text.strip():
            return "Tôi không nghe rõ bạn nói gì. [cười] Bạn có thể nói lại được không?"
            
        response = self.chat.send_message(user_text)
        return response.text
