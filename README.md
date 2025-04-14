Project 3 Released: April 3rd  
Wireframes Due: Thursday, April 10th at 5pm on Gradescope  
Diagrams Due: Thursday, April 17th at 5pm on Gradescope  
Code and Report Due: Thursday, April 24th at 5pm on Blackboard and Gradescope  
Use up to three late days on this project.  
You may work in pairs  

1 Introduction  
This project will have you implementing the game Connect Four in an application that can be played over the network.  

2 Connect Four  
Connect Four is a two player board game where players alternate dropping tokens into a 2D grid.  
Players may select which column to place their token, and all tokens will fall to the lowest available spot in the column.  
A player wins when they are able to get four of their tokens in a row.  
If all spaces in the 2D grid are filled and no one has been able to get four-in-a-row, the game ends in a draw.  
The rulebook to the game is attached as an appendix to this document.  

3 Your work  
Work for this project will be submitted as three components:  
- Wireframes due on April 10th to Gradescope  
- Class diagrams submitted to Gradescope on April 17th  
- Code and final report due to Blackboard and Gradescope on April 24th at 5pm  

You may use up to three late days on this project.  

3.1 Code  
Your submission should contain two zip files containing Maven projects: one for the server and one for the client.  

At a minimum, your code must support the following behavior:  
• A Client application that allows you to connect to a server to play against another human opponent.  
  - Your code may assume that there is a running server to connect to.  
  - Users should be able to play multiple games and the correct game state should be maintained.  
• Server code, which may optionally contain a GUI, will match clients against each other for play and keep track of the game state for each pairing.  
  - Server code will always be run before a client connects.  

Your combined project must meet the minimum requirements below:  
a) The server should be able to support multiple pairs of clients each playing a game of Connect Four simultaneously.  
b) The app should correctly determine a win, loss or draw for each game and at the end of each game, there should be an option to either play again with the same player or quit.  
c) The client must use a GUI with graphical elements. The GUI should be intuitive and all user actions should be clearly labeled.  
d) The server code should print a log of all activity on the server. A GUI for the server application may be useful here, but is not required.  
e) Users must create a unique username when logging on to the server. Duplicate usernames should give an error message to the user and prompt them to enter a new name.  
f) Players in a game should be able to send text messages to each other.  
g) All scenes in the app should be reachable and neither the client nor the server should freeze or crash during gameplay.  

3.2 Above and Beyond  
Completing the minimum requirements for this project will earn an 80%.  
To earn above this, you or your team must extend the minimum requirements.  

Here is a non-exhaustive list of ways you might extend the requirements to earn more points, roughly sorted from simplest to most complex:  
• Implement a non-trivial AI to allow single-user play.  
• Keep track of wins and losses for the usernames that persists even if the server and client are closed and reopened.  
• Implement a username and password logon screen for server connections  
• Allow users to add friends and see when they are online  

If you or your team completed any Above and Beyond components, you should include this in your report explaining your design decisions and how the user experience would be improved by your addition.  

3.3 Best in Show  
Only the top three projects for this class will receive a 100 on the project.  
These will be awarded on my judgment alone and there are no regrades.  
Top three announcements will be made in the last week in class and those teams will be asked to give a short 10-minute presentation on their project on the final day of class, May 1st.  
Additionally, the top three projects will have their code made available to the class.  

4 Submitting your work  
For the wireframes:  
- Create a PDF of your wireframe and submit the file to Gradescope by April 10th at 5pm.  
- If you are working in a team, submit as a group on Gradescope and submit the team form.  
- Check the Blackboard/Piazza post on this project for the link.  

Class diagrams for your client and server:  
- Submit as a PDF to Gradescope by April 17th at 5pm.  
- These must be digitally generated.  
- Be sure to add your teammate if you have one when submitting.  

Final code submission:  
- Submit a zip of your project to Blackboard and Gradescope.  
- Make sure that the submission runs with the Maven command.  
- Perform a `mvn clean` before submitting.  

Late days:  
- Late days can be used for either the wireframes, the diagrams or the final submission.  
- Example: if you use two late days on the wireframes, you can use only one more day for any later part (3 total max).  
- Remember that you and your partner must have late days remaining if you plan to submit late.  

4.1 Working in Pairs  
If you plan to work in a pair, please fill out the Project Partners Form when you submit.  
The link is available on Blackboard.  
Only one team member needs to submit.  
Be sure that the Gradescope submissions include both partners as a group submission.  

4.2 Academic Dishonesty / ChatGPT  
Reminder: ChatGPT and other AI tools are **not allowed** on this project.  
If there is suspicion of ChatGPT use or other forms of academic dishonesty, you will be asked to explain your code personally.  
If you cannot explain any line of your code—its function or its purpose—you will receive a zero on the assignment and a letter grade drop.
