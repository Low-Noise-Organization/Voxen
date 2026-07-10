package io.lownoise.voxen.distribution;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PublisherFactory {

    private static final Map<String, Publisher> publishers = new ConcurrentHashMap<>();

    static {
        register(new LocalFilePublisher());
        register(new MavenCentralPublisher());
        register(new ArtifactoryPublisher());
        register(new NexusPublisher());
        register(new S3Publisher());
    }

    public static void register(Publisher publisher) {
        publishers.put(publisher.name(), publisher);
    }

    public static Publisher get(String name) {
        Publisher publisher = publishers.get(name);
        if (publisher == null) {
            throw new IllegalArgumentException(
                "Unknown publisher: " + name + "\n" +
                "Available publishers: " + publishers.keySet());
        }
        return publisher;
    }

    public static java.util.Set<String> available() {
        return publishers.keySet();
    }
}
