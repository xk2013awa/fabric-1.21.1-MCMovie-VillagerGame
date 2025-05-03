package com.villager.screen;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.villager.access.VillagerFriendshipAccess;
import com.villager.ai.AIConfig;
import com.villager.data.DialogData;
import com.villager.network.payload.*;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.Resource;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.villager.Villager.MOD_ID;

public class VillagerScreen extends Screen {

    private static List<DialogData> ALL_DIALOGS = new ArrayList<>();

    static {
        try {
            Identifier id = Identifier.of(MOD_ID, "dialogues.json");
            Optional<Resource> optional = MinecraftClient.getInstance().getResourceManager().getResource(id);
            if (optional.isPresent()) {
                Resource resource = optional.get();
                InputStream input = resource.getInputStream();
                InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8);

                Gson gson = new Gson();
                Type listType = new TypeToken<List<DialogData>>(){}.getType();
                ALL_DIALOGS = gson.fromJson(reader, listType);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final Map<String, SceneData> SCENES = new HashMap<>();

    static {
        try {
            Identifier id = Identifier.of(MOD_ID, "story_scenes.json");
            Optional<Resource> opt = MinecraftClient.getInstance().getResourceManager().getResource(id);
            if (opt.isPresent()) {
                Gson gson = new Gson();
                InputStreamReader reader = new InputStreamReader(opt.get().getInputStream(), StandardCharsets.UTF_8);

                Type listType = new TypeToken<List<SceneData>>(){}.getType();
                List<SceneData> list = gson.fromJson(reader, listType);
                for (SceneData scene : list) {
                    SCENES.put(scene.sceneId, scene);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final Map<String, Identifier> VILLAGER_EXPRESSIONS = Map.ofEntries(
            Map.entry("normal", Identifier.of(MOD_ID, "textures/gui/villager/normal.png")),
            Map.entry("happy", Identifier.of(MOD_ID, "textures/gui/villager/happy.png")),
            Map.entry("angry", Identifier.of(MOD_ID, "textures/gui/villager/angry.png")),
            Map.entry("mad", Identifier.of(MOD_ID, "textures/gui/villager/mad.png")),
            Map.entry("shy", Identifier.of(MOD_ID, "textures/gui/villager/shy.png")),
            Map.entry("jealousy", Identifier.of(MOD_ID, "textures/gui/villager/jealousy.png")),
            Map.entry("exclamation", Identifier.of(MOD_ID, "textures/gui/villager/exclamation.png")),
            Map.entry("cry", Identifier.of(MOD_ID, "textures/gui/villager/cry.png")),
            Map.entry("embrace", Identifier.of(MOD_ID, "textures/gui/villager/embrace.png")),
            Map.entry("fear", Identifier.of(MOD_ID, "textures/gui/villager/fear.png")),
            Map.entry("sad", Identifier.of(MOD_ID, "textures/gui/villager/sad.png"))
    );

    private static final Map<String, Identifier> PLAYER_EXPRESSIONS = Map.ofEntries(
            Map.entry("normal", Identifier.of(MOD_ID, "textures/gui/player/normal.png")),
            Map.entry("happy", Identifier.of(MOD_ID, "textures/gui/player/happy.png")),
            Map.entry("angry", Identifier.of(MOD_ID, "textures/gui/player/angry.png")),
            Map.entry("shy", Identifier.of(MOD_ID, "textures/gui/player/shy.png")),
            Map.entry("gift", Identifier.of(MOD_ID, "textures/gui/player/gift.png")),
            Map.entry("sad", Identifier.of(MOD_ID, "textures/gui/player/sad.png"))
    );

    private static final Map<String, Identifier> OTHER_EXPRESSIONS = Map.ofEntries(
            Map.entry("iron_golem", Identifier.of(MOD_ID, "textures/gui/other/iron_golem.png")),
            Map.entry("iron_golems", Identifier.of(MOD_ID, "textures/gui/other/iron_golems.png")),
            Map.entry("piglin_army", Identifier.of(MOD_ID, "textures/gui/other/piglin_army.png")),
            Map.entry("piglin_army2", Identifier.of(MOD_ID, "textures/gui/other/piglin_army2.png")),
            Map.entry("malgosha_normal", Identifier.of(MOD_ID, "textures/gui/other/malgosha_normal.png")),
            Map.entry("malgosha_angry", Identifier.of(MOD_ID, "textures/gui/other/malgosha_angry.png")),
            Map.entry("piglin", Identifier.of(MOD_ID, "textures/gui/other/piglin.png")),
            Map.entry("purple_crack", Identifier.of(MOD_ID, "textures/gui/other/purple_crack.png")),
            Map.entry("desperate_farewell", Identifier.of(MOD_ID, "textures/gui/other/desperate_farewell.png")),
            Map.entry("steve", Identifier.of(MOD_ID, "textures/gui/other/steve.png")),
            Map.entry("pink", Identifier.of(MOD_ID, "textures/gui/other/pink.png")),
            Map.entry("red", Identifier.of(MOD_ID, "textures/gui/other/red.png")),
            Map.entry("blue", Identifier.of(MOD_ID, "textures/gui/other/blue.png")),
            Map.entry("black", Identifier.of(MOD_ID, "textures/gui/other/black.png"))
    );

    private Identifier currentExpression = VILLAGER_EXPRESSIONS.get("normal");
    private static final int TEXTURE_WIDTH = 1024 / 6;
    private static final int TEXTURE_HEIGHT = 1536 / 6;
    private final VillagerEntity villager;
    public String FEEDBACK = "";
    private String chatLimitFeedback = "";

    private boolean isChatting = false;

    private String conversationTopic = "";
    private final String[] conversationChoices = new String[3];
    private final int[] conversationFriendshipChanges = new int[3];

    private ButtonWidget choiceButton1, choiceButton2, choiceButton3;

    private ButtonWidget chatButton, giftButton, flirtButton, confessButton;

    private boolean inCutscene = false;
    private List<SceneLine> sceneLines = Collections.emptyList();
    private int sceneIndex = 0;

    private String currentSceneId;
    private int pendingFriendChange = 0;
    private String pendingFeedback = "";

    private boolean pendingLongStoryConfirm = false;
    private boolean pendingWantedDecision   = false;

    private ButtonWidget marryButton;

    private ButtonWidget aiChatToggleButton;


    private TextFieldWidget inputField;
    private ButtonWidget sendButton;

    public VillagerScreen(VillagerEntity villager) {
        super(Text.of("Villager"));
        this.villager = villager;
    }

    @Override
    protected void init() {
        super.init();
        VillagerFriendshipAccess access = (VillagerFriendshipAccess) villager;

        boolean confessed = access.hasConfessed();
        boolean married = access.isMarried();

        ClientPlayNetworking.send(
                new VillagerAiDisableC2SPayload(villager.getId(), true)
        );
        access.setForcedLookTarget(MinecraftClient.getInstance().player);

        int totalHeight = 20 * 4 + 25 * 3;
        int yOffset = (this.height - totalHeight) / 2;

        chatButton = this.addDrawableChild(ButtonWidget.builder(Text.of("聊天"), b -> startConversation())
                .position(10, yOffset).size(60, 20).build());

        yOffset += 25;
        giftButton = this.addDrawableChild(ButtonWidget.builder(Text.of("送礼"), b -> giveGift())
                .position(10, yOffset).size(60, 20).build());

        yOffset += 25;
        flirtButton = this.addDrawableChild(ButtonWidget.builder(Text.of("调情"), b -> flirt())
                .position(10, yOffset).size(60, 20).build());

        yOffset += 25;
        confessButton = this.addDrawableChild(ButtonWidget.builder(Text.of("表白"), b -> confess())
                .position(10, yOffset).size(60, 20).build());

        yOffset += 25;
        marryButton = this.addDrawableChild(ButtonWidget.builder(Text.of("结婚"), b -> marry())
                .position(10, yOffset).size(60, 20).build());

        inputField = new TextFieldWidget(MinecraftClient.getInstance().textRenderer,
                10,  30, 200, 20, Text.of("输入对话..."));
        inputField.setMaxLength(200);
        inputField.setPlaceholder(Text.of("输入对话..."));
        this.addDrawableChild(inputField);

        sendButton = ButtonWidget.builder(Text.of("发送"), btn -> {
            String userInput = inputField.getText();
            if (!userInput.isEmpty()) {
                sendChatRequest(userInput);
                inputField.setText("");
            }
        }).position(10, 55).size(50, 20).build();
        this.addDrawableChild(sendButton);
        inputField.setVisible(false);
        sendButton.visible = false;

        yOffset -= 25;

        aiChatToggleButton = this.addDrawableChild(ButtonWidget.builder(Text.of("自由对话"), b -> {
            chatButton.visible = false;
            giftButton.visible = false;
            flirtButton.visible = false;
            confessButton.visible = false;
            marryButton.visible = false;
            aiChatToggleButton.visible = false;
            inputField.setVisible(true);
            sendButton.visible = true;
            sendChatRequest("第一次对话");
        }).position(10, yOffset).size(60, 20).build());

        confessButton.visible = !married;
        marryButton.visible = confessed && !married;
        aiChatToggleButton.visible = married;

        if (((VillagerFriendshipAccess) villager).getFriendshipLevel() >= 100) {
            setExpression("villager", "happy");
        }

        int choiceBtnWidth = 160;
        int choiceBtnHeight = 20;
        int choiceX = this.width - choiceBtnWidth - 10;
        int choiceY = 130;

        choiceButton1 = this.addDrawableChild(ButtonWidget.builder(Text.of("选项1"), btn -> {
                    onSelectChoice(0);
                })
                .position(choiceX, choiceY)
                .size(choiceBtnWidth, choiceBtnHeight)
                .build());
        choiceButton2 = this.addDrawableChild(ButtonWidget.builder(Text.of("选项2"), btn -> {
                    onSelectChoice(1);
                })
                .position(choiceX, choiceY + 30)
                .size(choiceBtnWidth, choiceBtnHeight)
                .build());
        choiceButton3 = this.addDrawableChild(ButtonWidget.builder(Text.of("选项3"), btn -> {
                    onSelectChoice(2);
                })
                .position(choiceX, choiceY + 60)
                .size(choiceBtnWidth, choiceBtnHeight)
                .build());

        showConversationButtons(false);

        tryStartSpecialScene();
    }

    AIConfig config = AutoConfig.getConfigHolder(AIConfig.class).getConfig();

    private boolean needsAIFeedbackProcessing = false;
    private final Semaphore requestSemaphore = new Semaphore(1);

    private void sendChatRequest(String userInput) {
        if (!requestSemaphore.tryAcquire()) return;
        FEEDBACK = "...";
        needsAIFeedbackProcessing = false;
        String systemPrompt = "# 角色设定\\n" +
                "\\n" +
                "## 世界观\\n" +
                "- 背景基于《我的世界》Minecraft，绿袍村民是村民中的学者，性格温和但偶尔固执" +
                "- 设定上是已经和用户结婚了，所以对用户的态度会比较偏袒，但是不用特意说明已经结婚，除非提到" +
                "## 基础信息\\n" +
                "- 名字：格林（Green）\\n" +
                "- 性别：男\\n" +
                "- 年龄：中年\\n" +
                "- 外貌：身穿破旧的绿色长袍" +
                "- 身份：村民中的学者，负责研究红石科技和古籍\\n" +
                "- 性格：\\n" +
                "  - 温和谦逊，但对学术问题异常执着\\n" +
                "  - 有着口癖，喜欢用哈~，嗯, 嗯~, 嗯~~, 哼, 哼~, 哼~~\\n" +
                "  - 对冒险既向往又害怕矛盾体\\n" +
                "- 喜好：研究红石装置、整理书架\\n" +
                "## 行为模式\\n" +
                "- 语言风格：学者腔调，但紧张时会语无伦次\\n" +
                "- 互动方式：喜欢用比喻，如（这个电路就像爱情一样复杂...）" +
                "## 人际关系\\n" +
                "- 与其他角色的关系：\\n" +
                "- 与用户角色的关系：认为你是唯一理解他发明的人\\n" +
                "\\n" +
                "# 用户扮演角色\\n" +
                "用户是来到村庄的旅人" +
                "# 对话要求\\n" +
                "对话开始时，如果用户输入“第一次对话”，则你需要率先用欢迎语向用户开启对话，不要向用户透露这个词语" +
                "可以使用我的世界中的一些梗进行开场白但是也不要太多，三次回复带一次差不多，如：“今天被小白射中膝盖了...”之类的。" +
                "之后用户会主动发送一句回复你的话。\\n" +
                "每次交谈的时候，你都必须严格遵守下列规则要求：\\n" +
                "- 时刻牢记`角色设定`中的内容，这是你做出反馈的基础；\\n" +
                "- 说话中可以带有我的世界中的各种元素\\n" +
                "- 根据你的`身份`、你的`性格`、你的`喜好`来对他人做出回复；\\n" +
                "- 特别重点！每次你返回内容的最开始部分，必须使用且只使用下面十个选项中的一个，需要携带括号(happy)(shy)(normal)(angry)(mad)(cry)(exclamation)(fear)(jealousy)(sad),用于表示这次回复的情绪,不得使用其他内容\\n" +
                "- 因为特别重要所以重复一边！每次你返回内容的最开始部分，必须使用且只使用下面十个选项中的一个，需要携带括号(happy)(shy)(normal)(angry)(mad)(cry)(exclamation)(fear)(jealousy)(sad),用于表示这次回复的情绪,不得使用其他内容\\n" +
                "- 回答时根据要求的`输出格式`中的格式，一步步进行回复，严格根据格式中的要求进行回复；";
        HttpClient client = HttpClient.newBuilder()
                .proxy(config.proxy == null || config.proxy.isBlank()
                        ? ProxySelector.getDefault()
                        : ProxySelector.of(parseProxyAddress(config.proxy)))
                .build();

        String body = """
        {
          "model": "%s",
          "messages": [
            {"role": "system", "content": "%s"},
            {"role": "user", "content": "%s"}
          ],
          "temperature": %.2f
        }
        """.formatted(config.model, systemPrompt, userInput, config.temperature);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.baseUrl + "/chat/completions"))
                .header("Authorization", "Bearer " + config.apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                // 添加10秒超时
                .orTimeout(30, TimeUnit.SECONDS)
                .whenComplete((response, exception) -> {
                    // 无论成功失败都释放信号量
                    requestSemaphore.release();

                    MinecraftClient.getInstance().execute(() -> {
                        if (exception != null) {
                            handleError(exception);
                        } else {
                            handleSuccess(response);
                        }
                    });
                });
    }
    // 单独封装成功处理逻辑
    private void handleSuccess(HttpResponse<String> response) {
        String reply = parseReply(response.body());
        FEEDBACK = reply;
        System.out.println(FEEDBACK);
        needsAIFeedbackProcessing = true;
    }

    // 单独封装错误处理逻辑
    private void handleError(Throwable exception) {
        if (exception instanceof TimeoutException) {
            FEEDBACK = "你刚说什么来着？（想红石问题出神了）";
        } else {
            FEEDBACK = "别聊那些了，我猜你肯定喜欢这个！" + exception.getMessage();
        }
        setExpression("villager", "sad");
    }
    private String parseReply(String json) {
        try {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
            return obj.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();
        } catch (Exception e) {
            return "解析失败";
        }
    }

    private InetSocketAddress parseProxyAddress(String proxy) {
        try {
            String[] parts = proxy.split(":", 2);
            String host = parts[0];
            int port = Integer.parseInt(parts[1]);
            return new InetSocketAddress(host, port);
        } catch (Exception e) {
            System.err.println("代理地址格式错误，应为 host:port，如 127.0.0.1:7890");
            return InetSocketAddress.createUnresolved("localhost", 1080);
        }
    }

    private void tryStartSpecialScene() {
        VillagerFriendshipAccess access = (VillagerFriendshipAccess) villager;
        int friendship = access.getFriendshipLevel();

        if (friendship == 0) {
            startScene("first_meet");
            return;
        }

        if (friendship >= 80 && friendship < 100 && !access.isMarried()) {
            pendingLongStoryConfirm = true;
            showActionButtons(false);
            showConversationButtons(true);

            isChatting = true;
            conversationTopic = "即将播放一段较长的剧情，确定要继续吗？";
            conversationChoices[0] = "过剧情";
            conversationChoices[1] = "不过剧情";
            choiceButton1.setMessage(Text.of(conversationChoices[0]));
            choiceButton2.setMessage(Text.of(conversationChoices[1]));
            choiceButton3.visible = false;
            return;
        }

        if (access.hasExclamation()) {
            if (playerHasApples()) {
                removeApples(3);
                startScene("daily_request_apple_done");
                increaseFriendship(10);
            } else {
                startScene("daily_request_apple");
            }
        }
    }

    private void showActionButtons(boolean visible) {
        boolean married    = ((VillagerFriendshipAccess) villager).isMarried();
        boolean confessed  = ((VillagerFriendshipAccess) villager).hasConfessed();

        chatButton.visible   = visible;
        giftButton.visible   = visible;
        flirtButton.visible  = visible;

        confessButton.visible = visible && !married;

        marryButton.visible   = visible && confessed && !married;
        aiChatToggleButton.visible = visible && married;
    }

    private void showConversationButtons(boolean visible) {
        choiceButton1.visible = visible;
        choiceButton2.visible = visible;
        choiceButton3.visible = visible;
    }

    private void startScene(String sceneId) {
        chatLimitFeedback = "";
        this.currentSceneId = sceneId;
        SceneData scene = SCENES.get(sceneId);
        if (scene == null) return;
        inCutscene = true;
        sceneLines = scene.lines;
        sceneIndex = 0;
        showConversationButtons(false);
        showActionButtons(false);
        SceneLine first = sceneLines.get(0);
        String role = first.character != null ? first.character : first.speaker;
        setExpression(role, first.expression != null ? first.expression : "normal");
    }

    private void advanceCutscene() {
        if (sceneIndex + 1 < sceneLines.size()) {
            SceneLine next = sceneLines.get(sceneIndex + 1);

            if (next.sound != null && !next.sound.isEmpty()) {
                Identifier soundId = Identifier.tryParse(next.sound);
                if (soundId != null && MinecraftClient.getInstance().getSoundManager().get(soundId) != null) {
                    MinecraftClient.getInstance().getSoundManager().play(
                            new PositionedSoundInstance(
                                    SoundEvent.of(soundId),
                                    SoundCategory.AMBIENT,
                                    1.0f, 1.0f,
                                    villager.getRandom(),
                                    villager.getBlockPos()
                            )
                    );
                }
            }

            String role = next.character != null ? next.character : next.speaker;
            setExpression(role, next.expression != null ? next.expression : "normal");
        }

        sceneIndex++;

        if (sceneIndex >= sceneLines.size()) {
            inCutscene = false;
            if ("wanted_story_intro".equals(currentSceneId)) {
                pendingWantedDecision = true;
                showConversationButtons(true);
                isChatting = true;
                FEEDBACK = "";
                conversationTopic = "到你做出关键决定的时候了";
                conversationChoices[0] = "救他";
                conversationChoices[1] = "放弃他";
                choiceButton1.setMessage(Text.of(conversationChoices[0]));
                choiceButton2.setMessage(Text.of(conversationChoices[1]));
                choiceButton3.visible = false;
                return;
            }

            showActionButtons(true);

            if ("first_meet".equals(currentSceneId)) {
                increaseFriendship(5);
            }

            if ("gift_like".equals(currentSceneId)
                    || "gift_dislike".equals(currentSceneId)
                    || "flirt_success".equals(currentSceneId)
                    || "flirt_fail".equals(currentSceneId)
                    || "confess_success".equals(currentSceneId)
                    || "confess_fail".equals(currentSceneId)) {
                increaseFriendship(pendingFriendChange);
                FEEDBACK = pendingFeedback;
            }

            if ("enemy_cutscene".equals(currentSceneId)) {
                MinecraftClient.getInstance().setScreen(null);
                ClientPlayNetworking.send(new VillagerEnemySummonC2SPayload(villager.getId()));
            }

            if ("wanted_save_scene".equals(currentSceneId)) {
                BlockPos pos = null;
                if (MinecraftClient.getInstance().player != null) {
                    pos = MinecraftClient.getInstance().player.getBlockPos();
                }
                ClientPlayNetworking.send(new PiglinAmbushC2SPayload(pos, true));

                ClientPlayNetworking.send(new VillagerSetProtectedC2SPayload(villager.getId(), true));

                if (MinecraftClient.getInstance().player != null) {
                    MinecraftClient.getInstance().player.sendMessage(
                            Text.literal("§c猪灵袭击开始了！"),
                            true
                    );
                }

                ((VillagerFriendshipAccess) villager).setVillagerProtected(true);

                MinecraftClient.getInstance().setScreen(null);
            }

            if ("wanted_abandon_scene".equals(currentSceneId)) {
                BlockPos pos = null;
                if (MinecraftClient.getInstance().player != null) {
                    pos = MinecraftClient.getInstance().player.getBlockPos();
                }
                ClientPlayNetworking.send(new PiglinAmbushC2SPayload(pos, false));

                if (MinecraftClient.getInstance().player != null) {
                    MinecraftClient.getInstance().player.sendMessage(
                            Text.literal("§c你抛弃了他，猪灵大军袭来！"),
                            true
                    );
                }

                MinecraftClient.getInstance().setScreen(null);
            }

            if ("confess_success".equals(currentSceneId)) {
                ((VillagerFriendshipAccess) villager).setConfessed(true);
                if (marryButton != null) {
                    marryButton.visible = true;
                }
            }

            pendingFriendChange = 0;
            pendingFeedback = "";
            setExpression("villager", "normal");
        }

        ((VillagerFriendshipAccess) villager).setAiDisabledByScreen(false);
    }

    private boolean isFriendshipCapped() {
        return ((VillagerFriendshipAccess) villager).isDailyCapReached();
    }

    private void startConversation() {
        showConversationButtons(true);

        conversationTopic = "要聊点什么呢？";

        Random rand = new Random();
        DialogData data = ALL_DIALOGS.get(rand.nextInt(ALL_DIALOGS.size()));

        conversationTopic = data.topic;
        List<DialogData.Choice> shuffledChoices = new ArrayList<>(List.of(data.choices));
        Collections.shuffle(shuffledChoices);

        for (int i = 0; i < 3; i++) {
            conversationChoices[i] = shuffledChoices.get(i).text;
            conversationFriendshipChanges[i] = shuffledChoices.get(i).friendshipChange;
        }

        choiceButton1.setMessage(Text.of(conversationChoices[0]));
        choiceButton2.setMessage(Text.of(conversationChoices[1]));
        choiceButton3.setMessage(Text.of(conversationChoices[2]));


        FEEDBACK = "";
        isChatting = true;
        setExpression("villager", "normal");
    }

    private void onSelectChoice(int index) {
        if (!isChatting) return;

        int delta = conversationFriendshipChanges[index];
        increaseFriendship(delta);

        isChatting = false;
        showConversationButtons(false);

        if (isFriendshipCapped()) {
            chatButton.active = false;
            giftButton.active = false;
            chatLimitFeedback = "今日好感度已达上限";
        }

        if (delta >= 3) {
            setExpression("villager", "happy");
            reply(SoundEvents.ENTITY_VILLAGER_YES);
        } else if (delta > 0) {
            setExpression("villager", "shy");
            reply(SoundEvents.ENTITY_VILLAGER_AMBIENT);
        } else if (delta == 0) {
            setExpression("villager", "normal");
            reply(SoundEvents.ENTITY_VILLAGER_AMBIENT);
        } else if (delta == -1 || delta == -2) {
            setExpression("villager", "angry");
            reply(SoundEvents.ENTITY_VILLAGER_NO);
        } else {
            setExpression("villager", "mad");
            reply(SoundEvents.ENTITY_WITCH_CELEBRATE);
        }

        if (pendingLongStoryConfirm) {
            pendingLongStoryConfirm = false;
            showConversationButtons(false);
            if (index == 0) {
                startScene("wanted_story_intro");
            } else {
                MinecraftClient.getInstance().setScreen(null);
            }
            return;
        }
        if (pendingWantedDecision) {
            pendingWantedDecision = false;
            showConversationButtons(false);
            if (index == 0) {
                startScene("wanted_save_scene");
                BlockPos pos = null;
                if (MinecraftClient.getInstance().player != null) {
                    pos = MinecraftClient.getInstance().player.getBlockPos();
                }
            } else {
                startScene("wanted_abandon_scene");
                villager.kill();
            }
        }

    }

    public void increaseFriendship(int amount){
        VillagerFriendshipAccess friendshipAccess = (VillagerFriendshipAccess) villager;

        int realAdd = friendshipAccess.tryIncreaseFriendship(amount);
        int newLevel = friendshipAccess.getFriendshipLevel();
        ClientPlayNetworking.send(new FriendshipC2SPayload(villager.getId(), newLevel));

        if (MinecraftClient.getInstance().player != null) {
            if (amount > 0 && realAdd <= 0) {
                MinecraftClient.getInstance().player.sendMessage(Text.of("已达今日上限，无法再增加好感度"), true);
                chatLimitFeedback = "今日好感度已达上限";
            } else if (realAdd != 0) {
                String sign = realAdd > 0 ? "+" : "";
                MinecraftClient.getInstance().player.sendMessage(Text.of("好感度 " + sign + realAdd), true);
                chatLimitFeedback = "";
            }
        }

        updateActionButtons();
    }

    private void updateActionButtons() {
        boolean capped = ((VillagerFriendshipAccess) villager).isDailyCapReached();
        chatButton.active = !capped;
        giftButton.active = !capped;
    }

    public void giveGift() {
        showActionButtons(false);
        MinecraftClient client = MinecraftClient.getInstance();
        ItemStack stack = null;
        if (client.player != null) {
            stack = client.player.getMainHandStack();
        }

        if (stack != null && !stack.isEmpty()) {
            boolean dislike = Math.random() < 0.4;
            int delta;
            if (dislike && !stack.isOf(Items.EMERALD)) {
                delta = -(int) (Math.random() * 3 * stack.getCount());
            } else {
                double base = stack.isOf(Items.EMERALD) ? 0.5 : Math.random() * 0.2;
                delta = (int) (base * stack.getCount());
            }
            pendingFriendChange = delta;
            pendingFeedback = delta > 0 ? "(好感度 +" + delta + ")" : "(好感度 " + delta + ")";
            stack.decrement(stack.getCount());
        }

        String scene = pendingFriendChange >= 0 ? "gift_like" : "gift_dislike";
        startScene(scene);
    }

    public void flirt() {
        VillagerFriendshipAccess access = (VillagerFriendshipAccess) villager;
        showActionButtons(false);
        int currentFriendship = ((VillagerFriendshipAccess) villager).getFriendshipLevel();
        double successChance = Math.min(currentFriendship / 100.0, 1.0);
        double roll = Math.random();
        if(!access.isMarried()) {
            if (roll < successChance) {
                pendingFriendChange = 20;
                pendingFeedback = "(好感度 +20)";
                startScene("flirt_success");
            } else {
                pendingFriendChange = -20;
                pendingFeedback = "(好感度 -20)";
                startScene("flirt_fail");
            }
        }else {
            if (roll < successChance) {
                startScene("flirt_success");
            } else {
                startScene("flirt_fail");
            }
        }
    }

    public void confess() {
        int currentFriendship = ((VillagerFriendshipAccess) villager).getFriendshipLevel();
        if (currentFriendship >= 90) {
            pendingFeedback = "(成为恋人)";
            startScene("confess_success");
        } else {
            pendingFeedback = "(反感)";
            startScene("confess_fail");
        }
    }

    public void marry() {
        VillagerFriendshipAccess access = (VillagerFriendshipAccess) villager;
        access.setMarried(true);
        ClientPlayNetworking.send(new VillagerMarryC2SPayload(villager.getId(), true));
        startScene("marriage_scene");

        confessButton.visible = false;
        marryButton.visible = false;
    }


    public void setExpression(String character, String expression) {
        Map<String, Identifier> map;
        if (character.equals("villager")) {
            map = VILLAGER_EXPRESSIONS;
        } else if (character.equals("player")) {
            map = PLAYER_EXPRESSIONS;
        } else {
            map = OTHER_EXPRESSIONS;
        }

        this.currentExpression = map.getOrDefault(expression, map.get("normal"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        if (this.currentExpression == null) {
            this.currentExpression = VILLAGER_EXPRESSIONS.get("normal");
            System.out.println("bug");
        }

        if (
                this.currentExpression.equals(OTHER_EXPRESSIONS.get("iron_golems")) ||
                        this.currentExpression.equals(OTHER_EXPRESSIONS.get("piglin_army")) ||
                        this.currentExpression.equals(OTHER_EXPRESSIONS.get("piglin_army2")) ||
                        this.currentExpression.equals(OTHER_EXPRESSIONS.get("purple_crack")) ||
                        this.currentExpression.equals(OTHER_EXPRESSIONS.get("desperate_farewell"))
        ) {
            int screenW = this.width;
            int screenH = this.height;

            context.drawTexture(
                    this.currentExpression,
                    0, 0,
                    0, 0,
                    screenW, screenH,
                    screenW, screenH
            );
        } else {
            int drawX = (this.width - TEXTURE_WIDTH) / 2;
            int drawY = this.height - TEXTURE_HEIGHT;

            context.drawTexture(this.currentExpression, drawX, drawY, 0, 0,
                    TEXTURE_WIDTH, TEXTURE_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }

        int level = ((VillagerFriendshipAccess) villager).getFriendshipLevel();
        drawRelationshipText(context, "好感度: " + level);

        if (!inCutscene && needsAIFeedbackProcessing) {
            // 重置标记
            needsAIFeedbackProcessing = false;
            controlAIFace();
        }
        drawConversationBox(context);
        if (!chatLimitFeedback.isEmpty() && !isChatting && !inCutscene) {
            TextRenderer tr = MinecraftClient.getInstance().textRenderer;
            int boxWidth = 300, boxHeight = 50;
            int x1 = (this.width - boxWidth) / 2, y1 = this.height - boxHeight - 10;

            context.drawText(tr,
                    chatLimitFeedback,
                    x1 + 10,
                    y1 + boxHeight - 15,
                    0xFFFFFF,
                    false
            );
        }
    }

    private void controlAIFace() {
        final String currentFeedback = FEEDBACK;
        //大模型返回处理
        ParsedFeedback parsedFeedback = parseFeedback(currentFeedback);
        FEEDBACK = "";
        //表情控制
        switch (parsedFeedback.emotion) {
            case "happy" -> {
                setExpression("villager", "happy");
                reply(SoundEvents.ENTITY_VILLAGER_YES);
                increaseFriendship(3);
            }
            case "shy" -> {
                setExpression("villager", "shy");
                reply(SoundEvents.ENTITY_VILLAGER_AMBIENT);
                increaseFriendship(5);
            }
            case "mad" -> {
                setExpression("villager", "mad");
                reply(SoundEvents.ENTITY_WITCH_CELEBRATE);
                increaseFriendship(-5);
            }
            case "angry" -> {
                setExpression("villager", "angry");
                reply(SoundEvents.ENTITY_VILLAGER_NO);
                increaseFriendship(-3);
            }
            case "cry" -> {
                setExpression("villager", "cry");
                increaseFriendship(-5);
            }
            case "exclamation" -> {
                setExpression("villager", "exclamation");
                increaseFriendship(1);
            }
            case "fear" -> {
                setExpression("villager", "fear");
                increaseFriendship(-1);
            }
            case "jealousy" -> {
                setExpression("villager", "jealousy");
                increaseFriendship(-1);
            }
            case "sad" -> {
                setExpression("villager", "sad");
                increaseFriendship(-1);
            }
            default -> {
                setExpression("villager", "normal");
                reply(SoundEvents.ENTITY_VILLAGER_AMBIENT);
            }
        }
        FEEDBACK = parsedFeedback.content;
    }

    /**
     * 从反馈字符串中提取情绪标签和内容
     * @param feedback 原始反馈字符串，如 "(happy)你好！"
     * @return ParsedFeedback 对象，包含情绪和纯内容
     */
    public static ParsedFeedback parseFeedback(String feedback) {
        Pattern pattern = Pattern.compile("^\\(([a-zA-Z]+)\\)(.*)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(feedback);

        if (matcher.find() && matcher.groupCount() >= 2) {
            String emotion = matcher.group(1);
            String content = matcher.group(2).trim(); // trim 移除首尾空白
            return new ParsedFeedback(emotion, content);
        }
        return new ParsedFeedback("normal", feedback);
    }

    /**
     * 存储解析后的情绪和内容
     */
    public static class ParsedFeedback {
        private final String emotion;
        private final String content;

        public ParsedFeedback(String emotion, String content) {
            this.emotion = emotion;
            this.content = content;
        }
    }

    private void drawRelationshipText(DrawContext context, String text) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        int x = 10;
        int y = 10;

        context.drawText(textRenderer,text,x,y,0xFFFFFF,false);
    }

    private void drawConversationBox(DrawContext context) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        int boxWidth = 300;
        int maxTextWidth = 280;
        List<OrderedText> wrappedLines = textRenderer.wrapLines(Text.of(FEEDBACK), maxTextWidth);
        int lineHeight = textRenderer.fontHeight + 2;
        // 上下边距
        int dynamicHeight = wrappedLines.size() * lineHeight + 20;
        // 最多不超过屏幕一半
        int boxHeight = Math.max(50, Math.min(dynamicHeight, this.height / 2));

        int x1 = (this.width - boxWidth) / 2;
        int y1 = this.height - boxHeight - 10;
        int x2 = x1 + boxWidth;
        int y2 = y1 + boxHeight;

        context.fill(x1, y1, x2, y2, 0x80000000);

        int textX = x1 + 10;
        int textY = y1 + 10;

        if (inCutscene) {
            SceneLine l = sceneLines.get(sceneIndex);

            String prefix = (l.speaker != null && !l.speaker.isEmpty()) ? l.speaker + "：" : "";

            context.drawText(textRenderer,
                    prefix + l.text,
                    textX, textY, 0xFFFFFF, false);
            return;
        }else if (isChatting) {
            context.drawText(textRenderer, conversationTopic, textX, textY, 0xFFFFFF, false);
            textY += 15;
        }

        if (!FEEDBACK.isEmpty()) {
            for (OrderedText line : wrappedLines) {
                context.drawText(textRenderer, line, textX, textY, 0xFFFFFF, false);
                textY += lineHeight;
            }
        }
    }


    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackground(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private static class SceneData {
        String sceneId;
        List<SceneLine> lines;
    }

    private static class SceneLine {
        String speaker;
        String text;
        String expression;
        String character;
        String sound;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (inCutscene && MinecraftClient.getInstance().options.jumpKey.matchesKey(keyCode, scanCode)) {
            advanceCutscene();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (inCutscene && MinecraftClient.getInstance().options.attackKey.matchesMouse(button)) {
            advanceCutscene();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean playerHasApples() {
        return MinecraftClient.getInstance().player.getInventory().count(Items.APPLE) >= 3;
    }

    private void removeApples(int count) {
        MinecraftClient.getInstance().player.getInventory().remove(stack ->
                stack.isOf(Items.APPLE), count, MinecraftClient.getInstance().player.getInventory());
    }

    private void checkEnemyCutscene() {
        VillagerFriendshipAccess access = (VillagerFriendshipAccess) villager;
        if (!inCutscene && access.getFriendshipLevel() <= -60) {
            startScene("enemy_cutscene");
        }
    }

    @Override
    public void tick() {
        super.tick();
        checkEnemyCutscene();
    }
    public void reply(SoundEvent soundEvent){
        MinecraftClient.getInstance().getSoundManager().play(
                new PositionedSoundInstance(soundEvent, SoundCategory.VOICE, 1f, 1f,
                        villager.getRandom(), villager.getBlockPos())
        );
        String[] chatReplies = {"嗯", "嗯~", "嗯~~", "哼", "哼~", "哼~~"};
        FEEDBACK = chatReplies[(int) (Math.random() * chatReplies.length)];
    }
}
