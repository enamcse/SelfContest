# SelfContest
If judge data is available, then one can do the contest in ``C++`` easily.

## Behind the scene
Now, I want to discuss it a bit broadly. 
* I like to participate in programming contests. I felt that, just after the on-site contests, the problems would not 
be available in online judges. Sometimes, judges reveal the judge data, but in several files. 
Checking all this files manually is a little bit painful work for me. 
* I did not found any suitable ``CONTEST MANAGER`` available on the internet. I believe there exists some, but they
have some dependencies which were not supportive to me. Say: 
  * there is a contest manager named ``EJUDGE``, but it is in ``RUSSIAN`` which is not a suitable language for me!
  * Some contest manager runs on ``LINUX`` platform and I have only ``WINDOWS`` platform.
  * Some of them could not be configured because of my lack of knowledge about dependencies and others which I could not
  resolve.
*  ``PC^2`` is availabe on the internet, but they does not support multiple judge data for a single problem and there source
code is not public.

So, I have written this ``JAVA`` program to reduce my painful work.

## Goal
The followings are the main goals of initiating this project:
* Open Source platform for setting up programming contests
* Supporting as many as programming languages can be covered up
* Submissions from remote PC

## Completed Works
Till now, I could cover the following works:
* A contest can be set up from a PC. 
* Supports ``Single User`` at a time. 
* Needs manual Saving. Contest is saved as an ``Object``.
* Submitted ``Solution`` runs on different thread.
* Only one ``Solution`` could ``RUN`` at a time.
* Only Supports ``C++`` language.

## Plans in near future
The ``GOAL`` would be achieved by step by step planning. The best prioritized works are given below.
* ``Socket Prgramming`` is not applied till now. It would be implemented for using from other device connected in the LAN.
* Supporting ``Multiple User`` access.
* Auto Saving.
* Judge should have ``archieved`` copy of each submitted solution.
* Language support for ``JAVA``.
* Several solution could ``RUN`` at the same time.

## Faced Problems
While executing my goal, I faced the following problems and questions:
* There is no standard open source code available on the internet to give the verdict of a ``submission`` as far I searched. 
Specially for giving verdict ``RUNTIME ERROR``. I have made up a logic but do not know it would cover all cases or not. 
I am very eager to know how it is actully handled by the ``ONLINE JUDGE``s.
* How to maintain ``TIME LIMIT EXCEEDED`` by the ``OJ``s? How they ensure equality betweeb the ``submission``s which are 
submitted in the ``BUSY PERIOD`` and which are submitted in the ``IDLE PERIOD``. Here, ``BUSY PERIOD`` means when there are
some ``submission``s in the queue and some ``submission``s running parallelly.
* How the ``RUN TIME`` and ``MEMORY USED`` could be calculated? As a result, how could it gives ``MEMORY LIMIT EXCEEDED``
verdict.


# Requirement
There are some basic needs for this program. They are:

1. ``MY SQL`` should be installed having a database named ``contest`` which contains a table name ``users``. The table should
have four fields named ``users_id``, ``username``, ``password``and ``role``. The account of the database user should have
both username and password is ``root``. There are two types of role. ``Judge`` role would get all facilities.
2. A ``C++`` compiler should be installed and set into the environment ``PATH`` variable.
3. A ``JAVA`` JVM should be installed. JDK 8.
