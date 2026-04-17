package net.liukrast.repackaged.content.energy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

public class EnergyStack {
    public static final EnergyStack EMPTY = new EnergyStack(0, Optional.empty());

    public static final Codec<EnergyStack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("amount").forGetter(EnergyStack::getAmount),
            Codec.STRING.optionalFieldOf("owner").forGetter(EnergyStack::getOwner)
    ).apply(instance, EnergyStack::new));

    public static final StreamCodec<ByteBuf, EnergyStack> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, EnergyStack::getAmount,
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), EnergyStack::getOwner,
            EnergyStack::new
    );

    private int amount;
    private Optional<String> owner;

    public EnergyStack(int amount, Optional<String> owner) {
        this.amount = amount;
        this.owner = owner;
    }

    public Optional<String> getOwner() {
        return owner;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        if(this == EMPTY) return;
        this.amount = Math.max(0, amount);
    }

    public boolean isEmpty() {
        return amount == 0;
    }
}
