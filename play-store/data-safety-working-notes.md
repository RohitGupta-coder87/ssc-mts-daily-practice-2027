# Data Safety Working Notes

These notes are a conservative starting point, not a substitute for reviewing the current Play Console form and the Google Mobile Ads SDK disclosure page at submission time.

## Developer-controlled app data

- Account creation: No
- Name, phone, email collected by the app: No
- Photos, files or contacts requested: No
- Precise location requested: No
- Quiz history, bookmarks and streak: Stored locally on device; not sent to the developer
- Data deletion: User can clear app storage or uninstall

## Third-party advertising SDK

The included Google Mobile Ads SDK may collect or process data for advertising, analytics, fraud prevention, security and compliance. Re-check Google's current SDK disclosure before submitting. Categories that may need disclosure include:

- Device or other identifiers
- App interactions
- Advertising data
- Diagnostics
- Approximate location inferred from network information

Answer these according to the final SDK configuration, consent flow, serving mode, target countries and Google documentation. Do not submit the form from memory.

## Suggested Play Console declarations

- Contains ads: Yes
- App access: All core features available without login
- Target audience: Adults aged 18 and over; review the final audience selections carefully
- Government affiliation: None
- Financial features: None
- Health features: None
- News/magazine: No

## Before production

1. Add a public privacy-policy URL to Play Console and inside the listing.
2. Replace the placeholder contact email in the policy.
3. Configure user consent for every region where it is required before requesting ads.
4. Use real AdMob IDs only after approval; keep test IDs during development.
5. Complete and verify `app-ads.txt` using a domain you control or another compliant hosting method.
