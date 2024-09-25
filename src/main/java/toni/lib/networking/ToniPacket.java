package toni.lib.networking;


import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import toni.lib.utils.VersionUtils;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

#if AFTER_21_1
    import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
    import net.minecraft.network.codec.StreamCodec;
    import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
#else
    import toni.lib.networking.codecs.StreamCodec;
#endif

public abstract class ToniPacket <TPacket extends ToniPacket> #if AFTER_21_1 implements CustomPacketPayload #endif
{
    public ResourceLocation Resource;
    public StreamCodec<FriendlyByteBuf, TPacket> CODEC;

    #if AFTER_21_1
    public CustomPacketPayload.Type<TPacket> ID;

    @Override public Type<? extends CustomPacketPayload> type() { return ID; }
    #endif


    public ToniPacket(String modid, String path, StreamCodec<FriendlyByteBuf, TPacket> codec) {
        Resource = VersionUtils.resource(modid, path);
        CODEC = codec; //StreamCodec.composite(ByteBufCodecs.BOOL, SyncMediumcoreGameRuleMessage::mediumcoreMode, SyncMediumcoreGameRuleMessage::new);

        #if AFTER_21_1
        ID = new CustomPacketPayload.Type<>(Resource);
        #endif
    }

    public void registerType()
    {
        #if AFTER_21_1
        // In your common initializer method
        PayloadTypeRegistry.playS2C().register(ID, CODEC);
        #endif
    }

    public void registerClientHandler(BiConsumer<TPacket, LocalPlayer> consumer)
    {
        #if AFTER_21_1
        ClientPlayNetworking.registerGlobalReceiver(ID, (payload, context) -> consumer.accept(payload, context.player()));
        #else
        ClientPlayNetworking.registerGlobalReceiver(Resource, (client, handler, buf, responseSender) ->
        {
            var packet = CODEC.decode(buf);
            consumer.accept(packet, client.player);
        });
        #endif
    }

    public void registerClientHandler(Consumer<TPacket> consumer)
    {
        #if AFTER_21_1
        ClientPlayNetworking.registerGlobalReceiver(ID, (payload, context) -> consumer.accept(payload));
        #else
        ClientPlayNetworking.registerGlobalReceiver(Resource, (client, handler, buf, responseSender) ->
        {
            var packet = CODEC.decode(buf);
            consumer.accept(packet);
        });
        #endif
    }

    public void sendToAll(MinecraftServer server)
    {
        #if BEFORE_21_1
        FriendlyByteBuf buf = PacketByteBufs.create();
        CODEC.encode(buf, (TPacket) this);
        #endif

        for (ServerPlayer player : server.getPlayerList().getPlayers())
        {
            #if AFTER_21_1
            ServerPlayNetworking.send(player, this);
            #else
            ServerPlayNetworking.send(player, Resource, buf);
            #endif
        }
    }

    public void send(ServerPlayer player)
    {
        #if AFTER_21_1
        ServerPlayNetworking.send(player, this);
        #else
        FriendlyByteBuf buf = PacketByteBufs.create();
        CODEC.encode(buf, (TPacket) this);
        ServerPlayNetworking.send(player, Resource, buf);
        #endif
    }
}
