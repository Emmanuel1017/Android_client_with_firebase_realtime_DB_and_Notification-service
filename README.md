# Android_client_with_firebase_realtime_DB_and_Notification-service
This is an android client application receiving real-time updates from firebase's real-time DB updating on a recycle view with real-time custom notifications . 
However, the notification channel is through a local service listener started through a local broadcaster with a service intent function and an on-destroy listener to restart the listener service.   
However, the listener has some disadvantages : 
- It consumes power running in the background (however not that significant ) 
- Requires permission autostart to run efficiently and disabling of battery opimiozations. 
-In large-scale production, it is limited to 100,000 clients at an instance since it relies on  On-child added listeners to the real-time DB thus acting as an active DB client connection. 
-only fetches the last child on the DB at ago .😀 

Plamming to use firebase cloud messeging in future for better workaround in the notifications task.
