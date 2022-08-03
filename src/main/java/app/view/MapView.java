package app.view;

import app.controller.Simulator;
import app.model.*;

import static utils.Utils.FORMAT;
import static app.view.StyleUtils.*;

import app.model.interfaces.ElementOnMap;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.j2dviewer.J2DGraphRenderer;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;

import java.util.*;


/**
 *
 *      |==================================|
 *      |===========| MAP VIEW |===========|
 *      |==================================|
 *
 *   simulator for road map.
 *
 *
 *
 *  version 1: graph in one thread.
 *
 * @author  Kfir Ettinger & Amit Hajaj & Motti Dahari
 * @version 2.0
 * @since   2021-06-20
 */
public class MapView{
    /* MAP */
    protected final UserMap userMap;
    static protected final Hashtable<ElementOnMap, Node> elementsOnMapNodes;
    static protected final Graph displayGraph;

    /* DISPLAY */
    protected final Viewer viewer;
    private final ViewerPipe pipeIn;
    private static Sprite clock;

    /* SIMULATOR */
    private Simulator simulator;
    private static final long SLEEP_BETWEEN_FRAMES;
    public static final boolean DEBUG = true;
    private Date simulatorCurrTime;


    static{
        displayGraph = new MultiGraph("map simulation");
        elementsOnMapNodes = new Hashtable<>();
        SLEEP_BETWEEN_FRAMES = 2000;
    }

    /** CONSTRUCTORS */
    private MapView() {
        userMap = UserMap.INSTANCE;


        /* set graph */
        viewer = new Viewer(displayGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        pipeIn = viewer.newViewerPipe();
        viewer.addView("view1", new J2DGraphRenderer());
        viewer.disableAutoLayout();

        viewer.getView("view1").setShortcutManager(new MapShortcutManager());
        viewer.getView("view1").setMouseManager(new MapMouseManager());
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);
        pipeIn.addAttributeSink( displayGraph );

        displayGraph.addAttribute("ui.stylesheet", styleSheet);
        displayGraph.addAttribute("ui.quality");
        displayGraph.addAttribute("ui.antialias");


    }

    /** Singleton specific properties */
    public static final MapView instance = new MapView();

//    @Override
//    public void run() {
//        this.simulatorCurrTime = simulator.time();
//
//        pipeIn.pump();
//
//        // draw map components
//        drawMapComponents();
//        clock = drawClock();
//
//        do{
//            pipeIn.pump();
//
//            sleep();
//
//            drawElementsOnMap();
//
//        }while(simulator.isAlive());
//
//        System.out.println("Simulator show done.");
//    }

    public void init(Simulator simulator){
        this.simulator = simulator;

        pipeIn.pump();

        drawMapComponents();

        clock = drawClock();

        update();
    }

    public void update(){
        pipeIn.pump();

        clock.addAttribute("ui.label", FORMAT(simulator.time()));

        drawElementsOnMap();
    }

    private void drawElementsOnMap(){
        removeFinishedEvents();

        for (Drive drive : userMap.getOnGoingDrives()) {
            Node node = elementsOnMapNodes.get(drive);

            if (node == null) {
                node = displayGraph.addNode(String.valueOf(drive.getId()));
                elementsOnMapNodes.put(drive, node);
                drawElement(drive, node, "car");
                node.addAttribute("ui.style", randomGradientColor());
            } else {
                moveCar(drive, node);
            }

        }

        for (Rider request : userMap.getPendingRequests()) {
            Node node = elementsOnMapNodes.get(request);

            if (node == null) {
                node = displayGraph.addNode(String.valueOf(request.getId()));
                elementsOnMapNodes.put(request, node);
                drawElement(request, node, "rider");
            }
        }
    }

    private void removeFinishedEvents(){
        Iterator<ElementOnMap> eventIter = userMap.getFinishedEvents().iterator();

        while(eventIter.hasNext()){
            ElementOnMap nextEvent = eventIter.next();
            System.out.println("MapView remove finished event " + nextEvent);

            try {
                if(elementsOnMapNodes.containsKey(nextEvent)){
                    displayGraph.removeNode(String.valueOf(nextEvent.getId()));
                    elementsOnMapNodes.remove(nextEvent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            eventIter.remove();
        }
    }

//    private void sleep() {
//        simulatorCurrTime = new Date(simulatorCurrTime.getTime()+SLEEP_BETWEEN_FRAMES);
//        clock.addAttribute("ui.label", FORMAT(simulatorCurrTime));
//        try { Thread.sleep(SLEEP_BETWEEN_FRAMES) ; }
//        catch (InterruptedException e) { e.printStackTrace(); }
//    }

    private void drawMapComponents(){
        RoadMap.INSTANCE.getEdges().forEach(e -> {
            if(displayGraph.getEdge(String.valueOf(e.getId())) == null) {
                Node start = drawNode(e.getNode1());
                Node end = drawNode(e.getNode2());
                Edge displayEdge = displayGraph.addEdge(String.valueOf(e.getId()), start, end);//, e.isDirected());
                displayEdge.addAttribute("ui.class", "edge."+e.getHighwayType());
            }

        });
    }

    private Node drawNode(app.model.Node node){

        String keyStr = String.valueOf(node.getId());
        Node displayNode = displayGraph.getNode(keyStr);

        if(displayNode == null){

            displayNode = displayGraph.addNode(keyStr);
            displayNode.addAttribute("xy", node.getLongitude(), node.getLatitude());
            displayNode.addAttribute("ui.label", node.getId().toString());
                    }

        return displayNode;
    }



}