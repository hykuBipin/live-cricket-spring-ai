# 🏏 Live Cricket Spring AI App

An AI-powered real-time cricket commentary system built using Spring Boot, Spring AI, Ollama, and WebSockets.

This project simulates a Cricbuzz/Cricinfo-style live cricket score platform with AI-generated commentary and real-time updates.

---

# 🚀 Features

* ⚡ Real-time score broadcasting using WebSockets
* 🤖 AI-generated cricket commentary using Spring AI + Ollama
* 🏏 Cricbuzz-style live commentary flow
* 🔄 STOMP + SockJS integration
* 🌐 REST API for score updates
* 🎯 Clean Spring Boot architecture
* 📡 Event-driven live updates

---

# 🛠️ Tech Stack

| Technology     | Usage                 |
| -------------- | --------------------- |
| Java 17        | Backend               |
| Spring Boot 3  | Application framework |
| Spring AI      | AI integration        |
| Ollama         | Local LLM inference   |
| WebSockets     | Real-time updates     |
| STOMP + SockJS | WebSocket messaging   |
| Maven          | Build tool            |
| HTML/CSS/JS    | Frontend              |

---

# 📂 Project Structure

```text
src/
 ├── main/
 │    ├── java/com/example/livecricket/
 │    │     ├── config/
 │    │     │      └── WebSocketConfig.java
 │    │     ├── controller/
 │    │     │      └── ScoreController.java
 │    │     ├── service/
 │    │     │      └── CommentaryService.java
 │    │     └── LivecricketApplication.java
 │    │
 │    └── resources/
 │          ├── static/
 │          │      └── index.html
 │          └── application.properties
 │
 └── pom.xml
```

---

# ⚙️ Prerequisites

Install:

* Java 17+
* Maven
* Ollama

---

# 🧠 Install Ollama

Download:

https://ollama.com

Start Ollama:

```bash
ollama serve
```

Pull model:

```bash
ollama pull llama3
```

---

# 📦 pom.xml

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.4.5</version>
    </parent>

    <groupId>com.example</groupId>
    <artifactId>livecricket</artifactId>
    <version>1.0.0</version>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-websocket</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-starter-model-ollama</artifactId>
            <version>1.0.0</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

    </dependencies>

</project>
```

---

# 🚀 LivecricketApplication.java

```java
package com.example.livecricket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LivecricketApplication {

    public static void main(String[] args) {
        SpringApplication.run(LivecricketApplication.class, args);
    }
}
```

---

# 🔌 WebSocketConfig.java

```java
package com.example.livecricket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }
}
```

---

# 🤖 CommentaryService.java

```java
package com.example.livecricket.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class CommentaryService {

    private final ChatClient chatClient;

    public CommentaryService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String generate(String input) {

        String prompt = """
                Generate ONE short professional live cricket commentary line.

                Style:
                - Like Cricbuzz or Cricinfo
                - Maximum 20 words
                - Exciting
                - Realistic
                - No long paragraphs
                - No introductions
                - No explanations

                Score:
                %s
                """.formatted(input);

        return this.chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}
```

---

# 🏏 ScoreController.java

```java
package com.example.livecricket.controller;

import com.example.livecricket.service.CommentaryService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/score")
public class ScoreController {

    private final SimpMessagingTemplate template;
    private final CommentaryService ai;

    public ScoreController(SimpMessagingTemplate template,
                           CommentaryService ai) {
        this.template = template;
        this.ai = ai;
    }

    @PostMapping
    public Map<String, Object> update(@RequestBody Map<String, Object> score) {

        String text = score.toString();

        String commentary = ai.generate(text);

        Map<String, Object> response = Map.of(
                "score", score,
                "commentary", commentary
        );

        template.convertAndSend("/topic/live", response);

        return response;
    }
}
```

---

# 🌐 application.properties

```properties
spring.application.name=livecricket

spring.ai.ollama.chat.model=llama3

server.port=8080
```

---

# 🖥️ index.html

```html
<!DOCTYPE html>
<html>

<head>

    <title>Live Cricket AI Score</title>

    <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js"></script>

    <style>

        body{
            background:#0f172a;
            color:white;
            font-family:Arial;
            padding:30px;
        }

        .score-card{
            background:#1e293b;
            padding:20px;
            border-radius:12px;
            width:300px;
            box-shadow:0 4px 20px rgba(0,0,0,0.4);
        }

        .commentary{
            margin-top:20px;
            background:#111827;
            padding:15px;
            border-left:5px solid #22c55e;
            border-radius:8px;
            font-size:18px;
        }

    </style>

</head>

<body>

<h1>🏏 Live Cricket AI Score</h1>

<div class="score-card">

    <h2 id="team">Waiting...</h2>

    <h1 id="score">0/0</h1>

    <h3 id="overs">0 Overs</h3>

</div>

<div class="commentary" id="commentary">

    Waiting for commentary...

</div>

<script>

    const socket = new SockJS('/ws');

    const stompClient = Stomp.over(socket);

    stompClient.connect({}, function () {

        console.log("Connected");

        stompClient.subscribe('/topic/live', function (message) {

            const data = JSON.parse(message.body);

            document.getElementById("team").innerText =
                data.score.team;

            document.getElementById("score").innerText =
                `${data.score.runs}/${data.score.wickets}`;

            document.getElementById("overs").innerText =
                `${data.score.overs} Overs`;

            document.getElementById("commentary").innerText =
                data.commentary;
        });

    });

</script>

</body>

</html>
```

---

# ▶️ Run Application

Build:

```bash
mvn clean install
```

Run:

```bash
mvn spring-boot:run
```

Open browser:

```text
http://localhost:8080
```

---

# 🏏 Test API

```bash
curl -X POST http://localhost:8080/score \
-H "Content-Type: application/json" \
-d '{
  "team":"CSK",
  "runs":145,
  "wickets":2,
  "overs":"15.0"
}'
```

---

# 📡 WebSocket Endpoints

| Type      | Endpoint      |
| --------- | ------------- |
| WebSocket | `/ws`         |
| Topic     | `/topic/live` |
| REST API  | `/score`      |

---

# 👨‍💻 Author

Bipin Nair Gopalakrishnan

GitHub:
https://github.com/hykuBipin

```
```
