package online.sl.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.data.domain.Slice;

import java.io.IOException;

public class SliceJsonSerializer extends JsonSerializer<Slice<?>> {

    @Override
    public void serialize(Slice<?> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("content", value.getContent());
        gen.writeBooleanField("hasNext", value.hasNext());
        gen.writeEndObject();
    }

}

