# Play Store Publishing Checklist

## Account

- [ ] Create a Google Play Console Personal developer account
- [ ] Pay the one-time US$25 registration fee
- [ ] Complete legal-name, identity, phone and email verification
- [ ] Keep the developer phone and email working

## App identity

- [ ] Confirm final app name
- [ ] Confirm permanent package name: `in.rohitgupta.sscmtspractice`
- [ ] Add a working support email
- [ ] Host and verify the privacy-policy URL
- [ ] Keep a secure backup of the `.jks` upload key and password

## Build and testing

- [ ] Install Android SDK 36 and sync the project
- [ ] Test every quiz mode on at least one real Android phone
- [ ] Test airplane/offline mode; questions should still work
- [ ] Confirm all ads are test ads during development
- [ ] Generate a signed release Android App Bundle (`.aab`)
- [ ] Upload first to internal testing and review the automated pre-launch report
- [ ] Fix crashes, layout problems and accessibility warnings
- [ ] Run closed testing with at least 12 opted-in testers continuously for 14 days
- [ ] Collect real feedback and record what was changed
- [ ] Apply for production access only after the testing requirement is satisfied

## Store listing

- [ ] 512×512 app icon
- [ ] 1024×500 feature graphic
- [ ] At least two genuine phone screenshots
- [ ] Short and full descriptions
- [ ] Education category and appropriate tags
- [ ] Version 1.0.0 release notes
- [ ] Independent-app disclaimer; no government logo or official-looking claim

## App content forms

- [ ] Privacy policy
- [ ] Ads declaration: Yes
- [ ] App access: No login required
- [ ] Data safety form checked against the final AdMob SDK configuration
- [ ] Target audience and content rating questionnaire
- [ ] Government apps declaration: independent/non-government
- [ ] Content rights confirmed for every question and graphic

## Monetization

- [ ] Create/register the app in AdMob
- [ ] Replace Google's sample App ID and banner test ID only for production
- [ ] Configure required consent choices before loading ads
- [ ] Publish and verify `app-ads.txt`
- [ ] Never click your own ads or encourage ad clicks

## Final review

- [ ] App title, icon, screenshots and actual functionality match
- [ ] No broken buttons, placeholder email or sample ad IDs in production
- [ ] No copied paid notes, misleading earnings promises or “official SSC” claims
- [ ] Version code incremented for every new uploaded bundle
