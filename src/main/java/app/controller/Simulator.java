package app.controller;


import java.util.Date;

import app.view.MapView;
import utils.JsonHandler;
import app.model.graph.RoadMap;
import app.model.users.UserMap;

import static app.controller.RoadMapHandler.CreateMap;
import static app.controller.UserMapHandler.initRandomEvents;
import static utils.LogHandler.LOGGER;
import static utils.Utils.validate;


public class Simulator implements Runnable, SimulatorThread {
    private final Latch latch;
    private static long SLEEP_BETWEEN_FRAMES;
    private double speed;
    private boolean show;

    private EventManager eventManager;
    private MatchMaker cupid;
    private Thread eventsThread, cupidThread;
    private Date localTime;
    private final MapView mapView = MapView.instance;


    /* CONSTRUCTORS */

    private Simulator(){
        SLEEP_BETWEEN_FRAMES = 2000;
        this.latch = Latch.INSTANCE;
    }

    /** Singleton specific properties */
    public static final Simulator INSTANCE = new Simulator();

    public void init(double simulatorSpeed, int requestNum, int driveNum, boolean show, boolean bounds, boolean createFromPBF){
        validate(simulatorSpeed > 0, "Illegal simulator speed "+ simulatorSpeed + ".");
        RoadMapHandler.setBounds(bounds);

        if (createFromPBF) {
            LOGGER.info( "Start parsing main map.");
            CreateMap();
            JsonHandler.RoadMapType.save();
        } else {
            LOGGER.info( "Read road map from JSON.");
            JsonHandler.RoadMapType.load();
        }

        LOGGER.info("Map is ready. Map = " + RoadMap.INSTANCE);

//         initEventsInLine(requestNum);
        initRandomEvents(driveNum, requestNum);

        this.speed = simulatorSpeed;
        this.show = show;

    }



    /* RUN */

    @Override
    public void run() {
        localTime = UserMap.INSTANCE.getFirstEventTime();

        register(this);

        this.eventManager = new EventManager();
        this.cupid = new MatchMaker();
        if(show) {
            mapView.init(this);

            updateFrame();
            sleep(SLEEP_BETWEEN_FRAMES);
        }

        // start simulator
        eventsThread = new Thread(eventManager);
        eventsThread.start();

        // start cupid
        cupidThread = new Thread(cupid);
        cupidThread.start();


        latch.lock();

        do{
//            System.out.println("Simulator");
            latch.waitOnCondition();

            if(show) {
                updateFrame();
            }

            sleep(SLEEP_BETWEEN_FRAMES);
        }while(isAlive());

        finish();
    }

    private void finish(){
        unregister(this);
        LOGGER.info("Simulator finished!.");
    }

    private void updateFrame(){
        mapView.update();
    }

    public boolean isAlive(){
        return eventsThread.isAlive() || !UserMap.INSTANCE.getDrives().isEmpty();
    }



    /* GETTERS */

    public Date time() {
        return localTime;
    }

    public double speed() {
        return speed;
    }

    public EventManager events() {
        return eventManager;
    }

    public Thread eventsThread() {
        return eventsThread;
    }

    public Thread cupidThread() {
        return cupidThread;
    }



    @Override
    public Date getTime() {
        return localTime;
    }



    /* SETTERS */

    public void setSpeed(double simulatorSpeed) {
        this.speed = simulatorSpeed;
    }

    public void setEventsManager(EventManager events) {
        this.eventManager = events;
    }

    @Override
    public void setTime(Date date) {
        this.localTime = date;
    }

}
