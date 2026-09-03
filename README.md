# SSC MTS Daily Practice 2027

Native Android quiz app created for SSC MTS aspirants by Rohit Kumar Gupta.

## Version 1 features

- 80 original practice questions: English, Mathematics, Reasoning and General Awareness
- Deterministic 25-question Daily Quiz
- Random 40-question Full Mock Test
- 15-question subject practice sets
- Hindi/Hinglish explanations after every answer
- Local score history, total accuracy and daily streak
- Question bookmarks and bookmark revision mode
- Offline question bank; no login or paid server required
- Google AdMob banner integration using official test IDs
- Google UMP consent flow and a privacy-options entry point when required
- Android 16 / API 36 target and Android App Bundle-ready project

## Important: ads are currently safe test ads

The project deliberately uses Google's official sample AdMob App ID and test banner unit. Test ads do not generate money, but they prevent accidental invalid traffic while developing. Replace both IDs only after creating an AdMob app and completing its readiness steps.

1. Replace the App ID in `app/src/main/AndroidManifest.xml`.
2. Replace `TEST_BANNER_ID` in `AdsManager.java` and rename it clearly.
3. Test again using an AdMob test device before production.
4. Complete `app-ads.txt`, privacy policy and Play Console Data safety declarations.

Never click your own live ads or ask users to click ads.

## Open and test

1. Install a current stable Android Studio version that supports API 36.
2. Open this project folder.
3. Allow Android Studio to install Android SDK 36 and Gradle 8.13 if prompted.
4. Sync the Gradle project.
5. Run on an emulator or Android phone (Android 6.0+).

## Generate the Play Store AAB

1. In Android Studio, select **Build → Generate Signed Bundle / APK**.
2. Select **Android App Bundle**.
3. Create a new `.jks` upload key and keep its password and backup private.
4. Choose the `release` build variant.
5. Upload the generated `.aab` to the closed testing track first.

Do not send the upload key or password in public messages. Losing the key can make future updates difficult.

## Permanent identifiers

- Application ID: `in.rohitgupta.sscmtspractice`
- Version code: `1`
- Version name: `1.0.0`
- Minimum SDK: `23`
- Target/compile SDK: `36`

The application ID becomes permanent after the first Play Store upload. Change it before uploading only if a different final ID is required.

## Content update

Questions are stored in `app/src/main/assets/questions.json`. Every question has:

- a unique ID;
- subject and topic;
- exactly four options;
- a zero-based correct-answer index;
- an explanation.

Validate the file with a JSON validator after every edit. Do not copy paid notes or third-party question banks without permission.

## Play Store materials

Drafts are in the `play-store` folder:

- listing copy;
- privacy policy HTML;
- data-safety working notes;
- publishing checklist.

The privacy policy must be hosted at a public, stable URL before production submission.
