package me.mrxeman.utilitypets;

import me.mrxeman.utilitypets.utils.TimeUnits;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import net.minecraftforge.common.ForgeConfigSpec.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mojang.text2speech.Narrator.LOGGER;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = Utilitypets.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static class Server {
        public final IntValue furnyChargeTime;
        public final IntValue furnyCooldown;
        public final ConfigValue<List<? extends String>> furnyFavoriteItems;
        public final ConfigValue<? extends String> furnyTurboItem;
        public final IntValue furnyTurboTimeAdded;
        public final IntValue furnyTurboMaxTime;

        public final ConfigValue<List<? extends String>> lucasFavoriteItems;

        Server(ForgeConfigSpec.Builder builder) {
            builder
                    .comment("Config values for Furny Utility Pet.")
                    .push("furny");

            furnyChargeTime = builder
                    .comment("Furny charge time is how long should furny wait until it shoots his fire coal after getting the target.")
                    .comment("Furny charge time (ticks): ")
                    .defineInRange("chargeTime", 20, 0, Integer.MAX_VALUE);

            furnyCooldown = builder
                    .comment("Furny cooldown is how long should furny wait before next shot.")
                    .comment("Furny cooldown (ticks): ")
                    .defineInRange("cooldown", 40,  0, Integer.MAX_VALUE);

            furnyFavoriteItems = builder
                    .comment("List of items that Furny will accept for taming.")
                    .defineListAllowEmpty("favoriteItems", convertItemsToStringList(Items.COAL, Items.CHARCOAL), Config::validateItemName);

            builder
                    .comment("Config values for Furny's turbo ability.")
                    .push("turbo");

            //noinspection StringTemplateMigration
            furnyTurboItem = builder
                    .comment("Item which is used to boost Furny.")
                    .define("item", "minecraft:" + Items.BLAZE_POWDER, Config::validateItemName);

            furnyTurboTimeAdded = builder
                    .comment("Amount of time to be added to the turbo time when clicked with the boost item.")
                    .comment("Turbo time added (ticks): ")
                    .defineInRange("timeAdded", TimeUnits.SECONDS.toTicks(5), 0, Integer.MAX_VALUE);

            furnyTurboMaxTime = builder
                    .comment("Max turbo time that can be stacked on Furny.")
                    .comment("Turbo max time (ticks): ")
                    .defineInRange("maxTime", TimeUnits.MINUTE.toTicks(1), 0, Integer.MAX_VALUE);

            builder.pop();

            builder.pop();

            builder
                    .comment("Config values for Lucas The Spider Utility Pet.")
                    .push("lucas");

            lucasFavoriteItems = builder
                    .comment("List of items that Lucas The Spider will accept for taming and breeding.")
                    .defineListAllowEmpty("favoriteItems", convertItemsToStringList(Items.ROTTEN_FLESH), Config::validateItemName);

            builder.pop();
        }
    }

    static final ForgeConfigSpec serverSpec;
    private static final Server server;
    static {
        final Pair<Server, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Server::new);
        serverSpec = specPair.getRight();
        server = specPair.getLeft();
    }

    public static int furnyChargeTime;
    public static int furnyCooldown;
    public static Set<Item> furnyFavoriteItems;
    public static Item furnyTurboItem;
    public static int furnyTurboTimeAdded;
    public static int furnyTurboMaxTime;

    public static Set<Item> lucasFavoriteItems;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        furnyChargeTime = server.furnyChargeTime.get();
        furnyCooldown = server.furnyCooldown.get();
        furnyFavoriteItems = convertItemListToSetList(server.furnyFavoriteItems);
        furnyTurboItem = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(server.furnyTurboItem.get()));
        furnyTurboTimeAdded = server.furnyTurboTimeAdded.get();
        furnyTurboMaxTime = server.furnyTurboMaxTime.get();

        lucasFavoriteItems = convertItemListToSetList(server.lucasFavoriteItems);
        log();
    }

    static void log() {
        LOGGER.info("Here are config values for UtilityPets:");
        LOGGER.info("- Furny Charge Time: {}", furnyChargeTime);
        LOGGER.info("- Furny Cooldown: {}", furnyCooldown);
        LOGGER.info("- Furny Favorite Items: {}", furnyFavoriteItems);
        LOGGER.info("- Lucas Favorite Items: {}", lucasFavoriteItems);
    }

    private static boolean validateItemName(final Object obj) {
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(ResourceLocation.parse(itemName));
    }

    private static List<String> convertItemsToStringList(Item... items) {
        return Arrays.stream(items).map(item -> "minecraft:" + item.toString()).collect(Collectors.toList());
    }

    private static Set<Item> convertItemListToSetList(ForgeConfigSpec.@NotNull ConfigValue<List<? extends String>> itemList) {
        return itemList.get().stream().map(itemName -> ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(itemName))).collect(Collectors.toSet());
    }
}
