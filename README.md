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
* Works in ``WINDOWS`` platform only (``Command Line`` commands).
* List of solved ``problem``s.

## Plans in near future
The ``GOAL`` would be achieved by step by step planning. The best prioritized works are given below.
* ``Socket Prgramming`` is not applied till now. It would be implemented for using from other device connected in the LAN.
* Supporting ``Multiple User`` access.
* Auto Saving.
* Judge should have ``archieved`` copy of each submitted solution.
* Language support for ``JAVA``.
* Several solution could ``RUN`` at the same time.
* Penalty for ``WRONG SUBMISSION``
* ``REJUDGE`` function.
* ``RANKLIST`` generation.

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

Anyone who knows the answer of any of the questions given are appreciated to send me e-mail at ``enamsustcse@gmail.com``.
Thanks in advance.

# Requirement
There are some dependencies which should be resolved before ''RUNNIG'' the software. They are:
* Latest ``JAVA`` should installed and set to the ``PATH`` variable of the Operationg System.
* A ``C++`` compiler should installed and set to the ``PATH`` variable of the Operationg System.
* ``MY SQL`` should be installed and a database name ``contest`` should exists. The password of the ``root`` user should be
``root``. Under the ``root`` user a database should be created which should have a table named ``users`` with the field 
named ``user_id``, ``username``, ``password``, ``role``. ``user_id`` should be ``INTEGER`` and all others are ``VERCHAR``.
An entry with ``role`` as ``Judge`` should be configured.

Thanks. Feel free to contact with me to give suggestion or reporting a bug or a matter which you like to discuss.
