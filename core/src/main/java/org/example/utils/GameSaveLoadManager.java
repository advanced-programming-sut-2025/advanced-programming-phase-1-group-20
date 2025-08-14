package org.example.utils;

import org.example.common.models.App;
import org.example.common.models.entities.Game;

import java.util.List;
import java.util.stream.Collectors;

/**
 * این کلاس به عنوان یک واسط (API) برای مدیریت ذخیره و بارگذاری بازی‌ها عمل می‌کند.
 * تمام عملیات به کلاس App که منطق اصلی را در خود دارد، واگذار می‌شود.
 */
public class GameSaveLoadManager {

    /**
     * این متد در حال حاضر کار خاصی انجام نمی‌دهد، زیرا مدیریت اصلی
     * در App.initialize() و FileStorage انجام می‌شود.
     */
    public static void initialize() {
        System.out.println("GameSaveLoadManager is ready to delegate to App class.");
    }

    /**
     * درخواست ذخیره بازی فعلی را به App ارسال می‌کند.
     * @return true در صورت موفقیت، در غیر این صورت false.
     */
    public static boolean saveCurrentGame() {
        // App.saveCurrentGame() دیگر مقدار boolean برنمی‌گرداند،
        // اما می‌توانیم فرض کنیم اگر خطایی رخ ندهد، عملیات موفق بوده است.
        // برای سازگاری، true برمی‌گردانیم.
        App.saveCurrentGame();
        return true;
    }


    public static boolean autosave() {
        if (App.getGame() != null) {
            // از متد saveGameWithName در App برای ذخیره با نام مشخص استفاده می‌کنیم
            return App.saveGameWithName("autosave");
        }
        return false;
    }


    public static boolean saveGameWithName(Game game, String customSaveName) {
        // اطمینان حاصل می‌کنیم که بازی فعلی همان بازی مورد نظر است
        App.setGame(game);
        return App.saveGameWithName(customSaveName);
    }

    /**
     * درخواست بارگذاری بازی فعلی (معمولاً آخرین بازی) را به App ارسال می‌کند.
     * @return آبجکت Game بارگذاری شده.
     */
    public static Game loadCurrentGame() {
        // می‌توان یک نام پیش‌فرض مانند "current_game" را در نظر گرفت
        return App.loadGameByName("current_game");
    }

    /**
     * درخواست بارگذاری بازی ذخیره‌شده خودکار (autosave) را به App ارسال می‌کند.
     * @return آبجکت Game بارگذاری شده.
     */
    public static Game loadAutosave() {
        return App.loadGameByName("autosave");
    }

    /**
     * درخواست بارگذاری یک بازی با نام مشخص را از App می‌کند.
     * @param saveName نام ذخیره.
     * @return آبجکت Game بارگذاری شده.
     */
    public static Game loadGame(String saveName) {
        return App.loadGameByName(saveName);
    }

    /**
     * لیست نام تمام بازی‌های ذخیره شده را از App دریافت می‌کند.
     * @return لیستی از نام‌های ذخیره.
     */
    public static List<String> listSavedGames() {
        // لیست بازی‌ها را از App می‌گیریم و فقط نام ذخیره آن‌ها را استخراج می‌کنیم.
        return App.getAllGames().stream()
            .map(Game::getSaveName)
            .filter(name -> name != null && !name.isEmpty() && !name.equals("autosave") && !name.equals("current_game"))
            .collect(Collectors.toList());
    }

    /**
     * درخواست بارگذاری تمام بازی‌ها را به App می‌دهد.
     */
    public static void loadAllGames() {
        App.loadAllGames();
    }

    /**
     * درخواست حذف یک بازی ذخیره شده را به App ارسال می‌کند.
     * @param saveName نام ذخیره برای حذف.
     * @return true در صورت موفقیت.
     */
    public static boolean deleteSavedGame(String saveName) {
        return App.deleteSavedGame(saveName);
    }

    /**
     * وضعیت ذخیره شدن بازی را از خود آبجکت بازی بررسی می‌کند.
     * @param game بازی مورد نظر.
     * @return true اگر بازی ذخیره شده باشد.
     */
    public static boolean isGameSaved(Game game) {
        return game != null && game.isSaved();
    }
}
