# workplacevote
Workplacevote is a deliberately insecure web application. It is an Idea Bank that can be used in a workplace. People can submit new Ideas anonymously and vote (like) other peoples ideas. The ideas with most votes will be used and it is up to the ADMIN to remove approved/disapproved ideas.

It is based on the cybersecuritybase-project and can be run just like it.

# Getting started

1. Get the source code: `git clone https://github.com/sohac0/workplacevote.git`
2. Import the project to Netbeans
3. Run the project from Netbeans (or alternatively run from command line: `mvn spring-boot:run` )


# User accounts

Three user accounts

| Name   |  Password |  Role |
|:------ |:--------- |:----- |
| alice  | wonderland       | USER  |
| bob  | uncle       | USER  |
| boss | man     | ADMIN |

# Issue: A8-Cross-Site Request Forgery (CSRF)
1. Log in as alice/wonderland¨
2. Click Create new idea"
3. Enter Topic "Good idea"
4. Enter description "more holiday for all"
5. Click 'submit new idea'
7. Hover over the text "Good idea" to get the item id number
8. Click "Create new idea"
9. Enter topic "bad idea" 
10. Enter description and replace the item ID number from url
```javascript 

<script>
var http = new XMLHttpRequest();
var url = "../suggestions/like/1";
http.open("POST", url, true);
http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

http.send("");
</script>
```

11. Click to submit the idea
12. See that when you or another user loads the page, they automatically like the chosen Idea

Fix: Enable Csrf by commenting out the line `http.csrf().disable();` in SecurityConfiguration.java


# Issue: A3-Cross-Site Scripting (XSS)
1. Log in as alice/wonderland
2. Click "Create new idea"
3. Enter some text for Topic
4. Enter Description: <script>alert('xss');</script> 
5. Click "submit new idea"
6. See the javascript alert appear

Fix: change both the `th:utext` to `th:text` in `src/main/resources/templates/main.html`
```javascript

<script>
var http = new XMLHttpRequest();
var url = "../suggestions/like/1";
http.open("POST", url, true);
http.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

http.send("");
</script>

```
# Issue: A2-Broken Authentication and Session Management + A6-Sensitive Data Exposure

1. Log in as alice/wonderland
2. Create an idea and Like it.
3. Logout
4. Login as bob/uncle
5. Create an idea for bob (use different topic at least), Like it.
6. Click "My account (bob)" link
7. See that it lists the idea Bob created.
8. Change the "username=bob" part to "username=alice" in the URL.
9. See that it now list's alice's Liked ideas, even though the system was supposed to be anonymous.

Fix for both A2 and A6:
As the form is supposed to only the user's own account information, we will only show info for the authenticated user.

1. Change `src/main/resources/templates/fragments/header.html` not to include username as parameter. IE. replace `@{/details(username=${#authentication.getPrincipal().getUsername()})}` with `@{/details}`
2. In `src/main/java/sec/project/controller/AccountDetailsController.java` change `public String load(Model model, @RequestParam String username) {` to `public String load(Model model, Authentication auth) {` 
3. change `.findByUsername(username);` to `.findByUsername(auth.getName)`
4. See that now the My Account -page only shows the Ideas the Authenticated user has created.


# Issue: A7-Missing Function Level Access Control
1. log in as bob/uncle
2. Create an issue if there arent any
3. See the source code of the /main page. Locate the TODO: comments "TODO: only show admin link (/admin) to users with ADMIN role"
4. Change the pageurl /main to /admin.
5. See how all ideas are listed and can be deleted. This should only be allowed by ADMIN role and Bob only has USER role.

Fix:
1. Add `@EnableGlobalMethodSecurity(prePostEnabled = true)` before `public class SecurityConfiguration` in `src/main/java/sec/project/config/SecurityConfiguration.java`
2. Add `@PreAuthorize("hasRole('ADMIN')")` before `admin(..)` and `delete(..)` methods in `src/main/java/sec/project/controller/SuggestionController.java`
3. see that now only boss/man can access the page and delete Ideas.


In a practical implementation it might be wise to tag all the methods for the intended roles so that an imperfect implementation to the general security configuration or login configuration will not expose unwanted functionalities. This issue tries to demonstrate that while comments are very useful for programmers when maintaining the software, it may be wise to limit the information in them. Perhaps an optimized production version of the app would even have all comments removed to reduce the amount of data transmitted to clients and to remove extra information that can be exploited by attackers.

END DISCLAIMER: 
The application is a bit silly and is only to be used for demonstrating the vulnerabilities. The application would require more features, improved database searches. You could probably DoS the current system by creating a lot of users and ideas. The database searches in the current version tend to process all suggestions from DB in memory at once, which should be replaced by more efficient parameterized queries. Data
