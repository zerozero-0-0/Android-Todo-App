# TodoX

TodoX は Kotlin と Jetpack Compose で実装したローカル Todo 管理アプリです。Room による永続化、WorkManager/AlarmManager による締切通知、デイリータスクの繰り越し、自動再スケジュールをサポートします。

## 開発環境

- Android Studio (Ladybug 以降) / Android Gradle Plugin 8.11.1
- Kotlin 2.2.20
- Android SDK Platform 35 (Android 15)
- JDK 17

初回はプロジェクトルートで以下を実行して Gradle Wrapper を生成してください（wrapper JAR は同梱していません）。

```bash
gradle wrapper --gradle-version 8.11.1
```

以降は通常どおり `./gradlew` を利用できます。

## ビルド & 実行

```bash
./gradlew :app:assembleDebug
```

Android Studio からは `Run > Run 'app'` で実機/エミュレータへインストールできます。初回起動時に通知チャンネルが作成されます。API 33 以上では通知権限を許可してください。

## テスト

- 単体テスト: `./gradlew test`
- Instrumented テスト (in-memory Room DAO): `./gradlew connectedAndroidTest`

## 主な機能

- タブ (今日 / すべて / 完了) での一覧表示
- Todo の追加・編集・削除・優先度設定・タグ・締切日時・デイリー切り替え
- デイリータスクのリセット時刻（DataStore で保存）
- 締切通知（WorkManager、近接時刻は AlarmManager で厳密通知）
- 端末再起動後のジョブ再登録（BootCompletedReceiver）

## 動作確認フロー

1. 設定画面で `resetHour` を現在時刻＋1分へ変更
2. `daily=true` の Todo を完了にして 1 分待機 → 未完へ戻ることを確認
3. 締切を 5 分後に設定 → 通知が発火することを確認

## ライセンス

このリポジトリは学習用途を想定しています。必要に応じて改変してください。
