#RideShare - Ride Sharing Simulator

## Background
Many students do not live in the same city as the University, thus they need to use public transportation or share a ride.
This situation gave us the idea for our project. We have started with the idea of building a whole app with client side that should be used by real users, and a server side.
<br/>
<br/>
After carefully considering the time frame, we have decided to build only the simulator that will match students that drive to University, with students that joins a ride to the University.


## Goals
1. Create a Simulator that will simulate drivers and riders behaviour for research purposes.
<br/>With the help of the simulator we could:
    1. Check performance of DeepPool models, and deterministic matching algorithms.
    2. Check the potential that exist in ride-sharing behaviour on people transportation decisions.
3. This simulator can also be a great infrastructure for other navigation or ride-sharing apps.(our first idea)

## Challenges

### Data
#### Problem Description
Every model needs data to train on. We didn't find **free** reliable data, so we have decided to create one. our goal was to create dataset that will emphasize **real** user's behaviour.
#### Solution
We made a survey that asks for drivers and riders(our testers were students at Ariel University) transportation behaviour, and based on it, we have created a synthetic dataset. The dataset that we made holds 540 user and 25,960 drives.

### Simulator
#### Problem description
we also need a simulator to test the model on visualize it. We wanted to own the map and not use any API, or 3-rd party app that we have no control on it.
#### Solution
We choose to use OSM service. OSM is an open-source map service that let you download the map and make any manipulations you want/need. With it, we could create a simple map that we could train and visualize our model on.
   <br/> ![(Simulator showing TLV map)](https://i.ibb.co/mC5Tmgb/Screen-Shot-2022-09-11-at-17-10-32.png)

### algorithms
#### Problem description
With the data and the simulator we now can build our models to find the best and valuable matching between drivers and riders.
<br/>Our goal was to find a matching that will satisfy two conditions:
1. Rider waiting time should be as low as possible.
2. Driver extra time added for picking-up passengers should be reasonable.
#### Solution
After online research on map services, we choose to use two famous search algorithms.[1](#Literature)
1. **Dijkstra** -  algorithm for finding the shortest paths between nodes in a graph.
2. **A*** - The same idea as Dijkstra, but can be used on high scale graphs due to its heuristic function.(We used Manhattan Distance)
<br/>With these two, we can find the fastest path between two nodes on weighted graph as we have here. And thanks to A*, we could also use our simulator on high scaled maps. 

### Training
When we will have the first three mentioned above, we need to train the model we built on the data we have. During the process we tune our algorithms to fit the best for our predefined requirements. We started with a simple generic Dijkstra and A*, and finally we arrived to the last version we have here. The current version is not the best we thought of, but considering the time frame we have, this is the best we could get. 

## Tools
We used different tools for this project. Some are functional and others are to manage the project.
1. The whole project was written in **Java**. JDK version 17 is a must.
2. The project uses **MVC design pattern**. Every component is using its own thread to get the best visual and performance experience.
3. The first is **Maven**. We used Maven to manage our project dependencies and libraries we worked with.
4. The Second is, **OSM** as mentioned above.
5. The third is **GraphStream**. This is a Java library for visualize graphs.


## Literature
1. [Google Maps search algorithm's](https://www.researchgate.net/publication/333117435_Google_Maps)
2. [OSM services Wiki and documentation](https://wiki.openstreetmap.org/wiki/Main_Page)
3. 

|id     |ask_time                    |pickup_time          |src                                                                                                                                |drop_time            |dest                                                                                                                            |total_time_waited|total_time_traveled|
|-------|----------------------------|---------------------|-----------------------------------------------------------------------------------------------------------------------------------|---------------------|--------------------------------------------------------------------------------------------------------------------------------|-----------------|-------------------|
|34     |Wed Aug 17 10:30:14 IDT 2022|17 Aug 2022, 10:50:20|Node{id = 336080802, coordinates = (32.0923558,34.777551100000004), adjacent = (2469720113, 340318978, 290614228)}                 |17 Aug 2022, 10:54:58|Node{id = 1696868586, coordinates = (32.0797185,34.8218982), adjacent = (1696868583, 1696868602, 365204018)}                    |20 Minutes       |4 Minutes          |
|33     |Wed Aug 17 10:26:35 IDT 2022|17 Aug 2022, 10:37:59|Node{id = 393981125, coordinates = (32.076725700000004,34.836853000000005), adjacent = (1306274861, 393981126, 1589421691)}        |17 Aug 2022, 10:40:56|Node{id = 1062246024, coordinates = (32.0664068,34.851999400000004), adjacent = (1062246113)}                                   |11 Minutes       |2 Minutes          |
|32     |Wed Aug 17 10:47:30 IDT 2022|17 Aug 2022, 10:58:21|Node{id = 384648219, coordinates = (32.064209600000005,34.775640800000005), adjacent = (34650802, 4020571610, 34650800, 384648220)}|17 Aug 2022, 10:58:36|Node{id = 412519635, coordinates = (32.078446500000005,34.775949100000005), adjacent = (412519569, 412519623, 412520370)}       |10 Minutes       |0 Minutes          |
|31     |Wed Aug 17 10:39:58 IDT 2022|17 Aug 2022, 10:46:05|Node{id = 1695278936, coordinates = (32.064705000000004,34.7965188), adjacent = (1695278938, 1695278941, 1695278934)}              |17 Aug 2022, 10:46:52|Node{id = 2916015774, coordinates = (32.070634000000005,34.7861406), adjacent = (4082010275, 2916015773)}                       |6 Minutes        |0 Minutes          |
|30     |Wed Aug 17 10:49:01 IDT 2022|17 Aug 2022, 11:12:33|Node{id = 342376103, coordinates = (32.0908466,34.785121700000005), adjacent = (342380436, 343611513, 342365168)}                  |17 Aug 2022, 11:16:06|Node{id = 3481757560, coordinates = (32.0617966,34.8153151), adjacent = (3481754430, 1672108500, 1672108598)}                   |23 Minutes       |3 Minutes          |
|29     |Wed Aug 17 10:26:59 IDT 2022|17 Aug 2022, 10:47:33|Node{id = 343212324, coordinates = (32.0965879,34.7760303), adjacent = (343212330, 343332322, 1683214076)}                         |17 Aug 2022, 10:53:18|Node{id = 1288192090, coordinates = (32.0536472,34.7894543), adjacent = (1288189657, 563951867, 1288192122, 1288192078)}        |20 Minutes       |5 Minutes          |
|28     |Wed Aug 17 10:48:07 IDT 2022|17 Aug 2022, 10:52:46|Node{id = 2417878815, coordinates = (32.0837707,34.8040255), adjacent = (3716084605, 2716672991)}                                  |17 Aug 2022, 10:56:38|Node{id = 549233531, coordinates = (32.070157300000005,34.7668958), adjacent = (549233546, 539655506, 549233516, 539655513)}    |4 Minutes        |3 Minutes          |
|27     |Wed Aug 17 10:41:14 IDT 2022|17 Aug 2022, 11:06:24|Node{id = 9318816512, coordinates = (32.0702669,34.8042637), adjacent = (5291368796, 9318822625, 9318816511)}                      |17 Aug 2022, 11:06:38|Node{id = 1696868642, coordinates = (32.082585900000005,34.821538600000004), adjacent = (1692479554, 1696868630, 1697393033)}   |25 Minutes       |0 Minutes          |
|26     |Wed Aug 17 10:26:14 IDT 2022|17 Aug 2022, 10:32:27|Node{id = 2982245689, coordinates = (32.098379900000005,34.8261437), adjacent = (4847866934, 2982246260)}                          |17 Aug 2022, 10:39:38|Node{id = 1574692656, coordinates = (32.0538759,34.7731821), adjacent = (2133458880, 1574692929, 1574692732)}                   |6 Minutes        |7 Minutes          |
|25     |Wed Aug 17 10:24:35 IDT 2022|17 Aug 2022, 10:48:16|Node{id = 412872360, coordinates = (32.09751,34.8007938), adjacent = (4649675001)}                                                 |17 Aug 2022, 10:51:42|Node{id = 1698150071, coordinates = (32.076574400000005,34.819627700000005), adjacent = (1698150112, 1698149806, 3996264768)}   |23 Minutes       |3 Minutes          |
|24     |Wed Aug 17 10:28:04 IDT 2022|17 Aug 2022, 10:54:53|Node{id = 1102992358, coordinates = (32.1232233,34.8163211), adjacent = (1100405239, 413898297)}                                   |17 Aug 2022, 10:59:38|Node{id = 767370243, coordinates = (32.084955300000004,34.8047811), adjacent = (767370229, 767370245, 293148806)}               |26 Minutes       |4 Minutes          |
|23     |Wed Aug 17 10:29:51 IDT 2022|17 Aug 2022, 10:34:58|Node{id = 1357163786, coordinates = (32.0647828,34.814959900000005), adjacent = (1698149597, 410066676, 1689353168, 2356320438)}   |17 Aug 2022, 10:40:30|Node{id = 57046703, coordinates = (32.0617485,34.7620401), adjacent = (1628415525, 57046702, 1319999680)}                       |5 Minutes        |5 Minutes          |
|22     |Wed Aug 17 10:35:03 IDT 2022|17 Aug 2022, 10:43:19|Node{id = 2862997452, coordinates = (32.0566371,34.7658445), adjacent = (580710939, 580709980, 2862997451)}                        |17 Aug 2022, 10:45:12|Node{id = 563970408, coordinates = (32.0482484,34.7887502), adjacent = (1227191, 563969813, 1288173022, 563969251)}             |8 Minutes        |1 Minutes          |
|21     |Wed Aug 17 10:32:57 IDT 2022|17 Aug 2022, 10:58:43|Node{id = 1187529982, coordinates = (32.1250477,34.834172), adjacent = (417647489, 1187530011)}                                    |17 Aug 2022, 11:08:14|Node{id = 563949004, coordinates = (32.052936800000005,34.7878248), adjacent = (1288198966, 355867364, 1288201529, 355867280)}  |25 Minutes       |9 Minutes          |
|20     |Wed Aug 17 10:33:29 IDT 2022|17 Aug 2022, 10:41:44|Node{id = 540438827, coordinates = (32.046715500000005,34.7593387), adjacent = (540437723, 2266143441, 2266143438)}                |17 Aug 2022, 10:54:04|Node{id = 1101782936, coordinates = (32.1230363,34.8227968), adjacent = (3255617766)}                                           |8 Minutes        |12 Minutes         |
|19     |Wed Aug 17 10:25:10 IDT 2022|17 Aug 2022, 10:52:36|Node{id = 418850768, coordinates = (32.107888200000005,34.8218686), adjacent = (418850763)}                                        |17 Aug 2022, 11:02:32|Node{id = 1208799485, coordinates = (32.0565317,34.7610611), adjacent = (1208799532, 1208799500, 870887834, 1208799502)}        |27 Minutes       |9 Minutes          |
|18     |Wed Aug 17 10:29:21 IDT 2022|17 Aug 2022, 10:36:36|Node{id = 900648374, coordinates = (32.066193600000005,34.8056143), adjacent = (849340368, 1672108892, 900648383, 3997365732)}     |17 Aug 2022, 10:39:40|Node{id = 393968942, coordinates = (32.071907100000004,34.8328885), adjacent = (2304887929, 2304887965, 6652075342, 1697477242)}|7 Minutes        |3 Minutes          |
|17     |Wed Aug 17 10:23:56 IDT 2022|17 Aug 2022, 10:30:23|Node{id = 1696894949, coordinates = (32.074601900000005,34.8037233), adjacent = (1696894975, 1696894928, 1696894908, 1696894951)}  |17 Aug 2022, 10:34:30|Node{id = 705604998, coordinates = (32.0662244,34.8341055), adjacent = (977325600, 1197556824, 977325599)}                      |6 Minutes        |4 Minutes          |
|16     |Wed Aug 17 10:24:53 IDT 2022|17 Aug 2022, 10:30:42|Node{id = 4017282095, coordinates = (32.0592042,34.7844677), adjacent = (1342949242, 1234762944)}                                  |17 Aug 2022, 10:39:20|Node{id = 1352477839, coordinates = (32.1001795,34.8514325), adjacent = (358430433, 2315562394, 2315562384, 3068621880)}        |5 Minutes        |8 Minutes          |
|15     |Wed Aug 17 10:23:06 IDT 2022|17 Aug 2022, 10:28:47|Node{id = 539628955, coordinates = (32.0699398,34.7679775), adjacent = (440060013, 539628952, 539627326)}                          |17 Aug 2022, 10:30:18|Node{id = 4170418936, coordinates = (32.059520500000005,34.7671057), adjacent = (514350359, 2108063278, 514357167)}             |5 Minutes        |1 Minutes          |
|Summary|--                          |--                   |--                                                                                                                                 |--                   |--                                                                                                                              |13 Minutes       |4 Minutes          |
