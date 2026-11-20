package com.cleverkeyboard.app.keyboard;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u00b8\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0011\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\f\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010!\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B%\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u0018\u0010o\u001a\u00020\u00072\u0006\u0010p\u001a\u00020\u00072\u0006\u0010q\u001a\u000202H\u0002J\u000e\u0010r\u001a\u0002072\u0006\u0010s\u001a\u00020\u000eJ \u0010t\u001a\u00020\u00072\u0006\u0010u\u001a\u00020\u00072\u0006\u0010v\u001a\u00020\u00072\u0006\u0010w\u001a\u000202H\u0002J(\u0010x\u001a\u0002072\u0006\u0010y\u001a\u00020z2\u0006\u0010{\u001a\u00020H2\u0006\u0010|\u001a\u00020.2\u0006\u0010}\u001a\u00020\nH\u0002J+\u0010~\u001a\u0002072\u0006\u0010y\u001a\u00020z2\b\u0010\u007f\u001a\u0004\u0018\u00010\u00152\u0006\u0010|\u001a\u00020.2\u0007\u0010\u0080\u0001\u001a\u00020\u0007H\u0002J\u001d\u0010\u0081\u0001\u001a\u0004\u0018\u0001042\u0007\u0010\u0082\u0001\u001a\u0002022\u0007\u0010\u0083\u0001\u001a\u000202H\u0002J\u001d\u0010\u0084\u0001\u001a\u0004\u0018\u0001042\u0007\u0010\u0082\u0001\u001a\u0002022\u0007\u0010\u0083\u0001\u001a\u000202H\u0002J\u0017\u0010\u0085\u0001\u001a\u0004\u0018\u00010P2\u0006\u0010G\u001a\u000204\u00a2\u0006\u0003\u0010\u0086\u0001J\u000b\u0010\u0087\u0001\u001a\u0004\u0018\u000104H\u0002J\u0011\u0010\u0088\u0001\u001a\u00020\u001c2\u0006\u0010p\u001a\u00020\u0007H\u0002J#\u0010\u0089\u0001\u001a\u00020\u001c2\u0006\u0010G\u001a\u0002042\u0007\u0010\u0082\u0001\u001a\u0002022\u0007\u0010\u0083\u0001\u001a\u000202H\u0002J\u0011\u0010\u008a\u0001\u001a\u0002072\u0006\u0010y\u001a\u00020zH\u0016J\u0013\u0010\u008b\u0001\u001a\u00020\u001c2\b\u0010\u008c\u0001\u001a\u00030\u008d\u0001H\u0016R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0014\u001a\u0004\u0018\u00010\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0016\u001a\u0004\u0018\u00010\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0017\u001a\u0004\u0018\u00010\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0018\u001a\u0004\u0018\u00010\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0019\u001a\u0004\u0018\u00010\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u001b\u001a\u00020\u001cX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u001d\"\u0004\b\u001e\u0010\u001fR\u001e\u0010!\u001a\u00020\u001c2\u0006\u0010 \u001a\u00020\u001c@BX\u0086\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u001dR\u000e\u0010\"\u001a\u00020\u001cX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010#\u001a\u00020\u001cX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010$\u001a\u00020\u001cX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b$\u0010\u001d\"\u0004\b%\u0010\u001fR\u001a\u0010&\u001a\u00020\u001cX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b&\u0010\u001d\"\u0004\b\'\u0010\u001fR\u000e\u0010(\u001a\u00020\u001cX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010)\u001a\u00020\u001cX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010*\u001a\u00020\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010+\u001a\u00020\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010,\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010-\u001a\u00020.X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010/\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u00100\u001a\u00020.X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u00101\u001a\u000202X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u00103\u001a\u0004\u0018\u000104X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\"\u00105\u001a\n\u0012\u0004\u0012\u000207\u0018\u000106X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b8\u00109\"\u0004\b:\u0010;R7\u0010<\u001a\u001f\u0012\u0013\u0012\u00110\u0007\u00a2\u0006\f\b>\u0012\b\b?\u0012\u0004\b\b(@\u0012\u0004\u0012\u000207\u0018\u00010=X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bA\u0010B\"\u0004\bC\u0010DRe\u0010E\u001aM\u0012\u0015\u0012\u0013\u0018\u000104\u00a2\u0006\f\b>\u0012\b\b?\u0012\u0004\b\b(G\u0012\u0015\u0012\u0013\u0018\u00010H\u00a2\u0006\f\b>\u0012\b\b?\u0012\u0004\b\b(I\u0012\u0013\u0012\u00110\u001c\u00a2\u0006\f\b>\u0012\b\b?\u0012\u0004\b\b(J\u0012\u0004\u0012\u000207\u0018\u00010FX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bK\u0010L\"\u0004\bM\u0010NR(\u0010O\u001a\u0010\u0012\u0004\u0012\u00020P\u0012\u0004\u0012\u000207\u0018\u00010=X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bQ\u0010B\"\u0004\bR\u0010DR\"\u0010S\u001a\n\u0012\u0004\u0012\u000207\u0018\u000106X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bT\u00109\"\u0004\bU\u0010;R.\u0010V\u001a\u0016\u0012\n\u0012\b\u0012\u0004\u0012\u00020P0W\u0012\u0004\u0012\u000207\u0018\u00010=X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bX\u0010B\"\u0004\bY\u0010DR\u000e\u0010Z\u001a\u000202X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010[\u001a\u000202X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\\\u001a\u000202X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010]\u001a\u00020\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010^\u001a\u00020HX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b_\u0010`\"\u0004\ba\u0010bR\u000e\u0010c\u001a\u00020\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010d\u001a\u00020\u001cX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010e\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010f\u001a\u00020gX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010h\u001a\u000202X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010i\u001a\u000202X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010j\u001a\b\u0012\u0004\u0012\u00020P0kX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010l\u001a\u00020mX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010n\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u008e\u0001"}, d2 = {"Lcom/cleverkeyboard/app/keyboard/CustomKeyboardView;", "Landroid/inputmethodservice/KeyboardView;", "context", "Landroid/content/Context;", "attrs", "Landroid/util/AttributeSet;", "defStyleAttr", "", "(Landroid/content/Context;Landroid/util/AttributeSet;I)V", "accentKeyPaint", "Landroid/graphics/Paint;", "bgPaint", "borderPaint", "currentTheme", "Lcom/cleverkeyboard/app/data/KeyboardTheme;", "deleteHandler", "Landroid/os/Handler;", "deleteRepeatRunnable", "Ljava/lang/Runnable;", "functionKeyPaint", "iconDelete", "Landroid/graphics/drawable/Drawable;", "iconEnter", "iconGlobe", "iconSearch", "iconShift", "iconShiftLocked", "isActionSearch", "", "()Z", "setActionSearch", "(Z)V", "<set-?>", "isCursorDragActive", "isHoldingDelete", "isKeyLongPressTriggered", "isShiftLocked", "setShiftLocked", "isSwipeTypingEnabled", "setSwipeTypingEnabled", "isSwiping", "isTrackingSpace", "keyLongPressHandler", "keyLongPressRunnable", "keyPaint", "keyRect", "Landroid/graphics/RectF;", "keyShadowPaint", "keyShadowRect", "lastSlideX", "", "longPressKey", "Landroid/inputmethodservice/Keyboard$Key;", "onContinuousDeleteListener", "Lkotlin/Function0;", "", "getOnContinuousDeleteListener", "()Lkotlin/jvm/functions/Function0;", "setOnContinuousDeleteListener", "(Lkotlin/jvm/functions/Function0;)V", "onCursorMoveListener", "Lkotlin/Function1;", "Lkotlin/ParameterName;", "name", "direction", "getOnCursorMoveListener", "()Lkotlin/jvm/functions/Function1;", "setOnCursorMoveListener", "(Lkotlin/jvm/functions/Function1;)V", "onKeyPreviewListener", "Lkotlin/Function3;", "key", "", "label", "isDown", "getOnKeyPreviewListener", "()Lkotlin/jvm/functions/Function3;", "setOnKeyPreviewListener", "(Lkotlin/jvm/functions/Function3;)V", "onKeySecondaryCharacter", "", "getOnKeySecondaryCharacter", "setOnKeySecondaryCharacter", "onSpaceLongPressListener", "getOnSpaceLongPressListener", "setOnSpaceLongPressListener", "onSwipeWordListener", "", "getOnSwipeWordListener", "setOnSwipeWordListener", "spaceAccumulatedDrag", "spaceDownX", "spaceDownY", "spaceHandler", "spaceLabel", "getSpaceLabel", "()Ljava/lang/String;", "setSpaceLabel", "(Ljava/lang/String;)V", "spaceLongPressRunnable", "spaceLongPressTriggered", "swipePaint", "swipePath", "Landroid/graphics/Path;", "swipeStartX", "swipeStartY", "swipeVisitedKeys", "", "textBounds", "Landroid/graphics/Rect;", "textPaint", "adjustAlpha", "color", "factor", "applyTheme", "theme", "blendColors", "color1", "color2", "ratio", "drawCenteredText", "canvas", "Landroid/graphics/Canvas;", "text", "rect", "paint", "drawTintedIcon", "drawable", "tintColor", "findKeyAt", "x", "y", "findLetterKeyAt", "getSecondaryCharacterForKey", "(Landroid/inputmethodservice/Keyboard$Key;)Ljava/lang/Character;", "getSpaceKey", "isColorLight", "isPointInsideKey", "onDraw", "onTouchEvent", "event", "Landroid/view/MotionEvent;", "app_debug"})
@kotlin.Suppress(names = {"DEPRECATION"})
public final class CustomKeyboardView extends android.inputmethodservice.KeyboardView {
    @org.jetbrains.annotations.NotNull()
    private com.cleverkeyboard.app.data.KeyboardTheme currentTheme;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint bgPaint = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint keyPaint = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint textPaint = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint accentKeyPaint = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint functionKeyPaint = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint borderPaint = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.RectF keyRect = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.RectF keyShadowRect = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Rect textBounds = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint keyShadowPaint = null;
    @org.jetbrains.annotations.Nullable()
    private android.graphics.drawable.Drawable iconShift;
    @org.jetbrains.annotations.Nullable()
    private android.graphics.drawable.Drawable iconShiftLocked;
    @org.jetbrains.annotations.Nullable()
    private android.graphics.drawable.Drawable iconDelete;
    @org.jetbrains.annotations.Nullable()
    private android.graphics.drawable.Drawable iconEnter;
    @org.jetbrains.annotations.Nullable()
    private android.graphics.drawable.Drawable iconSearch;
    @org.jetbrains.annotations.Nullable()
    private android.graphics.drawable.Drawable iconGlobe;
    private boolean isShiftLocked = false;
    private boolean isActionSearch = false;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String spaceLabel = "English (India)";
    private boolean isSwipeTypingEnabled = false;
    @org.jetbrains.annotations.Nullable()
    private kotlin.jvm.functions.Function1<? super java.util.List<java.lang.Character>, kotlin.Unit> onSwipeWordListener;
    @org.jetbrains.annotations.Nullable()
    private kotlin.jvm.functions.Function0<kotlin.Unit> onContinuousDeleteListener;
    @org.jetbrains.annotations.Nullable()
    private kotlin.jvm.functions.Function1<? super java.lang.Character, kotlin.Unit> onKeySecondaryCharacter;
    @org.jetbrains.annotations.Nullable()
    private kotlin.jvm.functions.Function3<? super android.inputmethodservice.Keyboard.Key, ? super java.lang.String, ? super java.lang.Boolean, kotlin.Unit> onKeyPreviewListener;
    private boolean isHoldingDelete = false;
    @org.jetbrains.annotations.NotNull()
    private final android.os.Handler deleteHandler = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.Runnable deleteRepeatRunnable = null;
    @org.jetbrains.annotations.Nullable()
    private android.inputmethodservice.Keyboard.Key longPressKey;
    private boolean isKeyLongPressTriggered = false;
    @org.jetbrains.annotations.NotNull()
    private final android.os.Handler keyLongPressHandler = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.Runnable keyLongPressRunnable = null;
    private boolean isSwiping = false;
    private float swipeStartX = 0.0F;
    private float swipeStartY = 0.0F;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Path swipePath = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.Character> swipeVisitedKeys = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Paint swipePaint = null;
    @org.jetbrains.annotations.Nullable()
    private kotlin.jvm.functions.Function0<kotlin.Unit> onSpaceLongPressListener;
    @org.jetbrains.annotations.Nullable()
    private kotlin.jvm.functions.Function1<? super java.lang.Integer, kotlin.Unit> onCursorMoveListener;
    private boolean isCursorDragActive = false;
    private boolean isTrackingSpace = false;
    private float spaceDownX = 0.0F;
    private float spaceDownY = 0.0F;
    private float lastSlideX = 0.0F;
    private float spaceAccumulatedDrag = 0.0F;
    private boolean spaceLongPressTriggered = false;
    @org.jetbrains.annotations.NotNull()
    private final android.os.Handler spaceHandler = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.Runnable spaceLongPressRunnable = null;
    
    @kotlin.jvm.JvmOverloads()
    public CustomKeyboardView(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.Nullable()
    android.util.AttributeSet attrs, int defStyleAttr) {
        super(null, null);
    }
    
    public final boolean isShiftLocked() {
        return false;
    }
    
    public final void setShiftLocked(boolean p0) {
    }
    
    public final boolean isActionSearch() {
        return false;
    }
    
    public final void setActionSearch(boolean p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getSpaceLabel() {
        return null;
    }
    
    public final void setSpaceLabel(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    public final boolean isSwipeTypingEnabled() {
        return false;
    }
    
    public final void setSwipeTypingEnabled(boolean p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final kotlin.jvm.functions.Function1<java.util.List<java.lang.Character>, kotlin.Unit> getOnSwipeWordListener() {
        return null;
    }
    
    public final void setOnSwipeWordListener(@org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function1<? super java.util.List<java.lang.Character>, kotlin.Unit> p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final kotlin.jvm.functions.Function0<kotlin.Unit> getOnContinuousDeleteListener() {
        return null;
    }
    
    public final void setOnContinuousDeleteListener(@org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final kotlin.jvm.functions.Function1<java.lang.Character, kotlin.Unit> getOnKeySecondaryCharacter() {
        return null;
    }
    
    public final void setOnKeySecondaryCharacter(@org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function1<? super java.lang.Character, kotlin.Unit> p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final kotlin.jvm.functions.Function3<android.inputmethodservice.Keyboard.Key, java.lang.String, java.lang.Boolean, kotlin.Unit> getOnKeyPreviewListener() {
        return null;
    }
    
    public final void setOnKeyPreviewListener(@org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function3<? super android.inputmethodservice.Keyboard.Key, ? super java.lang.String, ? super java.lang.Boolean, kotlin.Unit> p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final kotlin.jvm.functions.Function0<kotlin.Unit> getOnSpaceLongPressListener() {
        return null;
    }
    
    public final void setOnSpaceLongPressListener(@org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function0<kotlin.Unit> p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final kotlin.jvm.functions.Function1<java.lang.Integer, kotlin.Unit> getOnCursorMoveListener() {
        return null;
    }
    
    public final void setOnCursorMoveListener(@org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function1<? super java.lang.Integer, kotlin.Unit> p0) {
    }
    
    public final boolean isCursorDragActive() {
        return false;
    }
    
    private final android.inputmethodservice.Keyboard.Key getSpaceKey() {
        return null;
    }
    
    private final boolean isPointInsideKey(android.inputmethodservice.Keyboard.Key key, float x, float y) {
        return false;
    }
    
    private final android.inputmethodservice.Keyboard.Key findKeyAt(float x, float y) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Character getSecondaryCharacterForKey(@org.jetbrains.annotations.NotNull()
    android.inputmethodservice.Keyboard.Key key) {
        return null;
    }
    
    private final android.inputmethodservice.Keyboard.Key findLetterKeyAt(float x, float y) {
        return null;
    }
    
    @java.lang.Override()
    public boolean onTouchEvent(@org.jetbrains.annotations.NotNull()
    android.view.MotionEvent event) {
        return false;
    }
    
    public final void applyTheme(@org.jetbrains.annotations.NotNull()
    com.cleverkeyboard.app.data.KeyboardTheme theme) {
    }
    
    @java.lang.Override()
    public void onDraw(@org.jetbrains.annotations.NotNull()
    android.graphics.Canvas canvas) {
    }
    
    private final void drawCenteredText(android.graphics.Canvas canvas, java.lang.String text, android.graphics.RectF rect, android.graphics.Paint paint) {
    }
    
    private final void drawTintedIcon(android.graphics.Canvas canvas, android.graphics.drawable.Drawable drawable, android.graphics.RectF rect, int tintColor) {
    }
    
    private final int adjustAlpha(int color, float factor) {
        return 0;
    }
    
    private final int blendColors(int color1, int color2, float ratio) {
        return 0;
    }
    
    private final boolean isColorLight(int color) {
        return false;
    }
    
    @kotlin.jvm.JvmOverloads()
    public CustomKeyboardView(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super(null, null);
    }
    
    @kotlin.jvm.JvmOverloads()
    public CustomKeyboardView(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.Nullable()
    android.util.AttributeSet attrs) {
        super(null, null);
    }
}