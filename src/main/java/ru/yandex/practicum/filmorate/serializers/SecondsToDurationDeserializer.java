package ru.yandex.practicum.filmorate.serializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Duration;

public class SecondsToDurationDeserializer extends JsonDeserializer<Duration> {
    @Override
    public Duration deserialize (JsonParser parser, DeserializationContext context) throws IOException {
        Long seconds = parser.getLongValue();
        return Duration.ofSeconds(seconds);
    }
}
