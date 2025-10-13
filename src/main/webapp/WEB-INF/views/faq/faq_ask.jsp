<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>FAQ 챗봇</title>
    <style>
        body {
            font-family: "Noto Sans KR", sans-serif;
            background-color: #f5f6fa;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        .chat-container {
            background: #fff;
            width: 400px;
            height: 500px;
            border-radius: 16px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
            display: flex;
            flex-direction: column;
            overflow: hidden;
        }

        .chat-header {
            background-color: #007bff;
            color: white;
            text-align: center;
            padding: 12px;
            font-weight: bold;
        }

        .chat-body {
            flex: 1;
            padding: 16px;
            overflow-y: auto;
            display: flex;
            flex-direction: column;
            gap: 10px;
        }

        .message {
            max-width: 75%;
            padding: 10px 14px;
            border-radius: 12px;
            word-break: break-word;
        }

        .user-message {
            align-self: flex-end;
            background-color: #dcf8c6;
            border-top-right-radius: 0;
        }

        .ai-message {
            align-self: flex-start;
            background-color: #f1f0f0;
            border-top-left-radius: 0;
        }

        .chat-input {
            display: flex;
            border-top: 1px solid #ddd;
        }

        .chat-input input {
            flex: 1;
            border: none;
            padding: 10px;
            font-size: 14px;
            outline: none;
        }

        .chat-input button {
            background-color: #007bff;
            color: white;
            border: none;
            padding: 10px 16px;
            cursor: pointer;
            font-weight: bold;
            transition: background 0.2s;
        }

        .chat-input button:hover {
            background-color: #0056b3;
        }

        .faq-link {
            text-align: center;
            margin-top: 8px;
        }

        .faq-link a {
            font-size: 13px;
            color: #007bff;
            text-decoration: none;
        }

        .faq-link a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>

<div class="chat-container">
    <div class="chat-header">FAQ 챗봇</div>

    <div class="chat-body" id="chatBody">
        <c:forEach var="msg" items="${chatHistory}">
            <div class="message ${msg.role == 'user' ? 'user-message' : 'ai-message'}">
                    ${msg.content}
            </div>
        </c:forEach>
    </div>

    <form class="chat-input" action="/faq/ask" method="post">
        <input type="text" name="question" placeholder="질문을 입력하세요" required/>
        <button type="submit">전송</button>
    </form>

    <div class="faq-link">
<!--   <a href="add">FAQ 직접 등록하기</a> -->
        <a href="reset">대화 초기화</a>
    </div>
</div>
<script>
    // 페이지 로드 시 스크롤을 자동으로 맨 아래로 내리기
    const chatBody = document.getElementById('chatBody');
    chatBody.scrollTop = chatBody.scrollHeight;
</script>
</body>
</html>

