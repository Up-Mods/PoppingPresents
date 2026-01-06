package dev.upcraft.poppingpresents.init;

import dev.upcraft.poppingpresents.platform.IPlatform;
import dev.upcraft.poppingpresents.present.PresentType;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataSerializer;

public class PPEntityDataSerializers {

    public static final EntityDataSerializer<Holder<PresentType>> PRESENT_TYPE = IPlatform.INSTANCE.registerDataSerializer("present_type", EntityDataSerializer.forValueType(PresentType.HOLDER_STREAM_CODEC));
}
