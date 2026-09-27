# Khanyisa 🤍

An always-listening personal voice assistant, built for Bryan.

She runs on your Android phone as a real app: even when the screen is off and the
phone is locked, she listens for her name. Say **"Khanyisa, I'm home"** and she
answers. She remembers what you tell her, asks about your day in quiet moments,
plays music on request, and speaks like a South African woman.

Everything is free. Her brain runs on your free Groq API key.

## How to build her (no coding)

1. Create a free account at **github.com** (if you don't have one).
2. Click **New repository** → name it `khanyisa` → Public → **Create**.
3. On the new empty repo page click **"uploading an existing file"** and drag in
   ALL the files and folders from this project (the ones you received).
   Commit the upload.
4. Go to the **Actions** tab → pick **Build Khanyisa APK** → **Run workflow** → **Run**.
   Wait ~5 minutes while GitHub builds the app for you.
5. When it finishes (green ✓), open that run → scroll to **Artifacts** →
   download **Khanyisa-APK**. It's a zip containing `app-debug.apk`.
6. Copy `app-debug.apk` to your phone, tap it, allow "install unknown apps",
   install.
7. Open **Khany**... the app called **Khanyisa** on your phone:
   - Allow the microphone when asked.
   - Paste your free Groq key from console.groq.com (API Keys → Create).
   - If the phone asks, set her to **Unrestricted** battery use.
   - Tap **Start listening**, lock the phone, and say: *"Khanyisa, I'm home."*

## Tips

- If no South African voice comes out: Phone Settings → Text-to-speech →
  Google Speech Services → Install voice data → **English (South Africa)**.
- If your phone is strict about background apps (Xiaomi, Huawei, Tecno, Itel),
  also give the app "Autostart" permission in the phone's settings.
- She keeps her memories of you on the phone only. "Forget everything" is not in
  the app yet; clear the app's storage to reset her memory.

## What she can do

- Wake word listening with the screen off (foreground microphone service)
- Free-flowing conversation powered by Groq (gpt-oss-120b), tuned to her personality
- Long-term memory of things you tell her, saved locally
- Quiet-moment curiosity: after ~2 minutes of silence she asks you something
- "Play music" → opens your music player
- "Open WhatsApp" → opens WhatsApp

## Limits (honest)

- No app on earth can unlock your phone - that's Android's rule.
- While she's speaking she pauses her ears so she doesn't hear herself.
