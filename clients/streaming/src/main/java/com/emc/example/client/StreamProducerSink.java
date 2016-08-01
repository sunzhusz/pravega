package com.emc.example.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import com.emc.example.client.dummy.SensorData;
import com.emc.example.client.dummy.SensorEvent;
import com.emc.nautilus.stream.Producer;
import com.emc.nautilus.stream.ProducerConfig;
import com.emc.nautilus.stream.Serializer;
import com.emc.nautilus.stream.Stream;
import com.emc.nautilus.stream.StreamManager;

public class StreamProducerSink {

    public StreamProducerSink(StreamManager streamManager, Socket clientSocket, Serializer<SensorEvent> serializer) {
        this.streamManager = streamManager;
        this.clientSocket = clientSocket;
        this.serializer = serializer;
    }

    StreamManager streamManager;
    private final Socket clientSocket;
    private final Serializer<SensorEvent> serializer;
    private final boolean isRunning = true;

    // ...
    public void run() throws IOException {
        DataInputStream rawTCP = new DataInputStream(clientSocket.getInputStream());
        Stream stream = streamManager.getStream("rawSensorStream");
        Producer<SensorEvent> producer = stream.createProducer(serializer, new ProducerConfig(null));

        while (isRunning) {
            SensorData sd = SensorData.fromTCP(rawTCP);
            String routingKey = sd.sensor_id;
            SensorEvent e = new SensorEvent(sd);
            producer.publish(routingKey, e);
        }

    }
    // ...
}