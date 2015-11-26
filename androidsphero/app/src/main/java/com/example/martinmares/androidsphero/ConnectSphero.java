package com.example.martinmares.androidsphero;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;

import com.orbotix.DualStackDiscoveryAgent;
import com.orbotix.Sphero;
import com.orbotix.classic.DiscoveryAgentClassic;
import com.orbotix.common.DiscoveryException;
import com.orbotix.common.Robot;
import com.orbotix.common.RobotChangedStateListener;

import org.glassfish.tyrus.client.ClientManager;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

public class ConnectSphero extends AppCompatActivity {

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private int counter = 0;
    private RingLog log;
    private SimpleSphero sphero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_sphero);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextView v = (TextView) findViewById(R.id.text_v);
        log = new RingLog(v, 20);
        sphero = new SimpleSphero(log);
       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int velocity = sphero.getVelocity();
                velocity += 2;
                if (velocity > 10) {
                    velocity = 2;
                }
                sphero.setVelocity(velocity);

            }
        });
        FloatingActionButton drw = (FloatingActionButton) findViewById(R.id.drw);
        drw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sphero.resetDrive();
            }
        });
        //Robot
        DiscoveryAgentClassic.getInstance().addRobotStateListener(new RobotChangedStateListener() {
            @Override
            public void handleRobotChangedState(Robot robot, RobotChangedStateNotificationType type) {
                switch (type) {
                    case Connected:
                        log.log("AAAAAA");
                        sphero.setSphero(new Sphero(robot));
                        log.log("BBBBBB");
                        connectToServer();
                        break;
                    case Disconnected:
                        sphero.setSphero(null);
                        break;
                    default:
                        log.log("Robot - " + type);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            DiscoveryAgentClassic.getInstance().startDiscovery(this);
        } catch( Exception e ) {
            log.log("ERROR: " + e);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if( sphero != null ) {
            sphero.disconnect();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_connect_sphero, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void connectToServer() {

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                log.log("Create client");
                System.out.println("Create client");
                ClientManager client = ClientManager.createClient();
                try {
                    log.log("Connecting to ws endpoint");
                    System.out.println("Connecting to ws endpoint");
                    client.connectToServer(new Endpoint() {
                        @Override
                        public void onOpen(Session session, EndpointConfig EndpointConfig) {
                            session.addMessageHandler(new MessageHandler.Whole<String>() {
                                @Override
                                public void onMessage(String message) {
                                    handleReceivedInstruction(message);
                                }
                            });
                        }
                    }, ClientEndpointConfig.Builder.create().build(), URI.create("ws://ec2-52-29-133-31.eu-central-1.compute.amazonaws.com:80/sphero"));
                    log.log("Connected to ws endpoint");
                    System.out.println("Connected to ws endpoint");
                } catch (DeploymentException | IOException e) {
                    log.log(e.toString());
                    e.printStackTrace();
                }
            }
        });
    }

    private void handleReceivedInstruction(String msg) {
     //   log.log("Received " + msg);
        EVENT_TYPE event = EVENT_TYPE.valueOf(msg);
        switch (event) {
            case LEFT_DOWN:
                sphero.setLeft(true);
                break;
            case LEFT_UP:
                sphero.setLeft(false);
                break;
            case UP_DOWN:
                sphero.setUp(true);
                break;
            case UP_UP:
                sphero.setUp(false);
                break;
            case RIGHT_DOWN:
                sphero.setRight(true);
                break;
            case RIGHT_UP:
                sphero.setRight(false);
                break;
            case DOWN_DOWN:
                sphero.setDown(true);
                break;
            case DOWN_UP:
                sphero.setDown(false);
                break;
            default:
                log.log("Unexpected event: " + msg);
        }
    }

    public enum EVENT_TYPE {
        LEFT_DOWN,
        LEFT_UP,
        UP_DOWN,
        UP_UP,
        RIGHT_DOWN,
        RIGHT_UP,
        DOWN_DOWN,
        DOWN_UP
    }
}
