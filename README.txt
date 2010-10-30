Retro Rockets Lunar Lander
v0.4 By Patrick M Brennan

What is this?
This is my own adaptation of the classic video game "Lunar Lander".  What I have here is actually a hybrid game, which is a cross between a classic video game and a spaceflight simulator.  I think I have struck the balance well and I have a game which is realistic without being boring or impossible to play.

My goals in writing this were as follows:
I wanted to honor the original Lunar Lander, which I loved.
I wanted to have the game displayed with a similar kind of "retro" sensibility, because I loved the old vector display of Lunar Lander.
I wanted to fly a model which was based on the Apollo Lunar Module's performance characteristics.
I wanted to have a display which scales (zooms) smoothly, which always shows the LM and the lunar surface, and in which the LM is always more or less in the center of the view.
I wanted to be able to play multiple scenarios, including “Lunar Lander classic”.
Finally, I wanted to have the option to take on the challenge of landing the Lunar Module from orbit, which is a very difficult task indeed. 
I wanted the user to play the situation (i.e. landing on the moon) rather than the controls (e.g. use an Apollo-style DSKY to handle the task).    I wanted to abstract out the task of landing on the moon, and separate it from the task of managing a particular user interface.  Although I have enormous respect and affection for the way that lunar landings were actually performed in real life, there's no  reason why a modern astronaut should have to know how to work the Apollo DSKY, and I wanted the player's focus on the task itself, not the UI.

As a tester of this program, I'm asking you to try this out and help me with making decisions about what to add, keep, or remove from the current product as it evolves toward something which is releasable.

What is here
This README, both as an OpenDocument and as plain text
A set of Java class files
Some miscellaneous data files
A font ("Digital Dream")
This font can also be found here:
http://www.1001freefonts.com/digitaldream.php

Getting ready to run
You must have a recent JVM installed.  (version 1.5 or higher.)  You can check the version of Java that you have installed by opening a command-line window and typing:

java -version

Java will respond with something that should begin like this:

java version "1.6.0_11"

You should install the included font file.  On Windows, this means putting the included font file into the directory C:\Windows\Fonts.

Put all the rest of these files into a single directory in a convenient place on your hard drive.  

In the future, I will implement a decent installer which will do all these things for you, but this is what we have to work with at the moment.

Running the game
from the command line, you can CD to the directory where you placed all the class files and type:

java -classpath bin net.pbrennan.Lander_2009.TestLMRunner2

Key Bindings
Key bindings will be displayed in the command-line window when you start the game.  Here is the current list:

Left          Turn left                        Apply counterclockwise torque on the spacecraft from the RCS thrusters                                                      
Right         Turn right                       Apply clockwise torque on the spacecraft from the RCS thrusters                                                             
Open Bracket  RCS Translate Left               Apply RCS Translation thrusters left                                                                                        
Close Bracket RCS Translate Right              Apply RCS Translation thrusters right                                                                                       
Back Slash    Cycle RCS Rotation Mode          Cycle between free and damp rotation modes.                                                               
Up            Increase thrust                  Increase main engine thrust                                                                                                 
Down          Decrease thrust                  Decrease main engine thrust                                                                                                 
Space         Thrust smart toggle              If main engine thrust is not zero, kill thrust.  If main engine thrust is zero, apply full thrust.                          
Q             Kill thrust                      Reduce main engine thrust to zero                                                                                           
Shift         Trim                             Apply the command but at a lower gain.  (Applies to turn and throttle commands)                                             
R             Toggle Landing Radar             Toggle the landing radar.
Escape        Pause                            Pause the game in progress                                                                                                  
T             Increase time acceleration       Increase the acceleration of time by a factor of 10, up to a maximum of 1000x.                                               
Y             Decrease time acceleration       Decrease the acceleration of time by a factor of 10, down to a minimum of 1x.                                               
1             Zoom to show entire moon         Adjust the zoom level to see the LM and the entire moon                                                                     
2             Zoom to show the landing target  Adjust the zoom level so that the landing target is on the screen.                                                          
3             Zoom to show the closest surface Adjust the zoom level to the highest level that includes both the LM and the lunar surface.                                 
4             Zoom to show the LM              Adjust the zoom level to see the LM close up.                                                                               
F12           Reset                            Reset the game to its starting state                                                                                        
F1            Help                             Display help                                                                                                                
P             Toggle Autopilot                 Toggle between autopilot on and off                                                                             
O             Cycle Autopilot Mode             Cycle between the available autopilot modes   

How to Land
For this test, you're just going to concentrate on landing.  The landing target has been preset, and is represented as a flashing red triangle on the moon map in the upper right corner of the screen.  (Choosing a different target will come later.)  You can also see the landing target in Zoom modes 1 and 2, and sometimes in mode 3 (if you're particularly close to the target).

I'm going to describe as little as possible here, with the hope that you can easily discern the elements of the user interface and use them appropriately.

Remember: if you want to start the scenario over again, you can always press F12 to reset.

You start in the Lunar Module, in a circular orbit 100km over the moon.  The LM starts out as the green dot in the top middle of the screen.

The basic plan, cribbed from Apollo, is:

1.Perform a Descent Orbit Insertion (DOI) to change your orbit from 100x100 km circular orbit to a 100x15km Descent Orbit.  The low point of this orbit (the perilune) is chosen to be about 400 km uprange of the landing target.
2.At the perilune of the orbit, begin Powered Descent Initiation (PDI).  This means you will open your throttle and descend to the lunar surface.  Your engine will be running from PDI until the moment of touchdown.
3.Null your horizontal velocity over the target and then enter vertical descent.
4.Touch down!

Therefore, here are the steps to follow to land on the moon:

1. Pitch to 180 degrees (use the left and right cursor keys).  To get precision control of your pitch and throttle, use the shift key while pressing either your turn or your throttle (up and down) keys.  (Alternatively, you may use the HOLD 180 or HOLD RETRO autopilot modes.  Hit O to cycle through the available autopilot modes until you have the correct one selected, and then press P to activate the autopilot.  I intend to eventually subtract points for using the autopilot, but there is no penalty at the present time.)

2. In a short while, you will see a gold square marked “DOI” approach your ship.  You can judge this best using scaling mode 3.  This is the DOI cue, telling you when it's time for the “Descent Orbit Insertion” maneuver.  This happens at longitude = 85.37 degrees.  In the center of the DOI box, trim your pitch back to 180 degrees and engage full thrust (press and release space bar) until your perilune (the lowest point in your orbit) has lowered to about 15,000 m.  Watch the gauge at the middle right.  (Don't let it get below zero -- that means you WILL crash into the moon!)  When it hits 15,000 m, hit space again to shut down your engine.  I usually trim with small bursts of thrust to get as close to 15,000 m as I can.  Sometimes I go well below 15km, in which case I turn the LM around to pitch=0.0 and thrust forward to trim the orbit up.  You can also turn the LM to vertical pitch (90 degrees) and use the RCS translation thrusters to trim your orbit very precisely.  (Note that Apollo often used the RCS translation thrusters to trim their orbits).

3. Wait for perilune.  This will happen 180 degrees from where you are now, on the opposite side of the moon.  (In this case we are aiming for a perilune at 265.37 degrees, because it will take about 405 km, or 13.37 degrees, to slow down to the landing site).  Getting there will take a little less than an hour, so you will most likely want to accelerate time with the "T" key.  Press it twice to accelerate to 100x and wait for perilune.  When you are getting close, switch back to real time by pressing the "Y" key twice.  Scaling mode 2 provides a nice show during this time, as the target gets closer and closer.

4. You will now see another cue approaching your ship, marked “PDI”.  (Scaling mode 2 or 3 will show this best) This is the PDI cue, telling you when it's time for “Powered Descent Initiation”.  When you're in the center of the PDI box, pitch to 180 degrees again and apply full throttle.  (Again, the autopilot HOLD180 or HOLD RETRO modes are good for this.)  The distance to target should read about 405 km.

5. You are now in powered descent.  Your task here is to null out your enormous horizontal velocity (about 1600 m/s or 1 mile per second) while watching your vertical velocity to make sure you're not descending too fast.  I find that a descent rate of about 20-30 m/s is a good place to hold it.  As long as your horizontal speed is more than about 10x your vertical speed -- in other words, as long as the cyan arrow in that dial on the upper left is mostly horizontal -- you will concentrate on holding your rate of descent steady and nulling as much horizontal speed as you can.

6. When you're below 10,000 m altitude, your radar will begin to return a signal from the surface.  At this point, you want to watch the radar altimeter instead of the datum altimeter.  The lower you are, the more your radar altitude is the important number, because that's where the ground is.

7. When the velocity arrow starts to bend significantly downward, you are now in terminal descent.  I think this is the hardest part of the game.  Your job now is to use your throttle and the direction of your thrust to simultaneously manage your horizontal and vertical speeds.  There's a gauge in the upper left corner of the window which tells you how much vertical thrust you are currently applying (on the left side) and how much is the minimum required to avoid smacking into the moon (on the right).  Below that is a similar gauge indicating how much horizontal thrust you are applying and the amount you need to apply to come to rest just at the target.

8. You can use the TERM DESC mode of the autopilot at this point.  It's not foolproof – for example, it isn't too smart about avoiding mountains.  However, it's quite good for holding the LM's attitude and throttle at reasonable settings during the period when managing them both well is critical.  TIP: the Terminal Descent mode is not fuel-efficient!

9. Land!  You should completely null out your horizontal speed (your RCS thrusters are good for trimming this out) and touch the ground at less than 3 m/s vertical and 1 m/s horizontal with a pitch angle of 90 +/- 6 degrees.  The game will rate your landing in the command line window.  If you land too fast, you will see an explosion as the lander disintegrates before your eyes.

10. You can start again by pressing F12 to reset the scenario.

Something else to do
When playing the old Lunar Lander, I used to want to see if I could escape the moon's gravity altogether with the LM, but of course I couldn't.  The original is programmed such that once you get too high, the game resets.  In this game, you can if you want to … but why would you want to? It's kind of pointless, isn't it?  You tell me.

Questions:
Is the game too easy to play?
Is it too hard to play?
Were you too frustrated to keep trying?
Are the directions clear?
Are the displays easy to read?  Do you understand what you're looking at?  Which ones didn't you get?
What would you do to improve the game?

Still TODO:
- More sounds
- Game start logic:
  - selecting a difficulty level
  - selecting a landing site
  - selecting whether the player starts in orbit or at the top of the terminal descent phase (more like the original Lunar Lander)
- Better recognition that a landing has happened
- Rate the landing (crash, hard, damage, good, perfect)
- Score computation
- In-game help
- More detailed terrain
- Varied missions
- Abort logic
- Welcome screen
- A decent installer.
- Port to another target?
  - C++ / DirectX?
  - Flash?
- Tooltips or other kind of help for the display
- anything else? 


