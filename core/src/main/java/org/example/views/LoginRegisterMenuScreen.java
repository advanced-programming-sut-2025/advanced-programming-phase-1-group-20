package org.example.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.example.Main;
import org.example.controllers.LoginRegisterMenuController;

public class LoginRegisterMenuScreen implements Screen {
    private final LoginRegisterMenuController controller;
    private Stage stage;
    private Skin skin;

    private TextButton registerButton;
    private TextButton loginButton;

    //Tables
    private Table table;
    private Table registerTable;
    private Table loginTable;

    // RegisterTable Buttons
    private Label randomPasswordLabel;

    private Label usernameLabel;
    private TextField username;

    private Label passwordLabel;
    private TextField password;

    private Label confirmPasswordLabel;
    private TextField confirmPassword;

    private Label nicknameLabel;
    private TextField nickname;

    private Label emailLabel;
    private TextField email;

    private Label genderLabel;
    private TextField gender;

    private TextButton submitRegisterButton;

    private TextButton backToMainRegisterButton;





    public LoginRegisterMenuScreen(LoginRegisterMenuController controller , Skin skin) {
        this.controller = controller;
        this.skin = skin;
        registerButton = new TextButton("Register", skin);
        loginButton = new TextButton("Login", skin);

        table = new Table();
        registerTable = new Table();
        loginTable = new Table();

        randomPasswordLabel = new Label("for random password type random in password field", skin);

        usernameLabel = new Label("username : ", skin);
        username = new TextField("", skin);

        passwordLabel = new Label("password : ", skin);
        password = new TextField("", skin);

        confirmPasswordLabel = new Label("confirm password : ", skin);
        confirmPassword = new TextField("", skin);

        nicknameLabel = new Label("nickname : ", skin);
        nickname = new TextField("", skin);

        emailLabel = new Label("email : ", skin);
        email = new TextField("", skin);

        genderLabel = new Label("gender : ", skin);
        gender = new TextField("", skin);

        controller.setView(this);
    }
    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        stage.clear(); // پاک کردن بازیگران موجود برای اطمینان از چیدمان تازه

        // روت تیبل اصلی که همه چیز رو نگه می‌داره و تمام صفحه رو پر می‌کنه
        Table rootContainer = new Table();
        rootContainer.setFillParent(true);
        rootContainer.center(); // محتوای روت تیبل رو در مرکز صفحه قرار می‌ده

        // تیبل اصلی شما که دکمه‌های Login و Register رو داره
        // نامش رو table حفظ می‌کنم، اما بهتره نام واضح‌تری مثل mainMenuTable داشته باشه
        table.clear(); // مطمئن شویم که table خالی است قبل از اضافه کردن مجدد عناصر
        table.center();
        table.row().pad(10, 0, 10, 0).width(100).height(25);
        table.add(loginButton);
        table.row().pad(10, 0, 10, 0).width(100).height(25);
        table.add(registerButton);


        rootContainer.add(table); // table شما که دکمه‌ها رو داره
        rootContainer.add(loginTable); // تیبل لاگین
        rootContainer.add(registerTable); // تیبل رجیستر

        stage.addActor(rootContainer); // فقط rootContainer رو به استیج اضافه می‌کنیم

        // تنظیمات مرکزیت برای تیبل‌های loginTable و registerTable
        registerTable.center();
        loginTable.center();

        // چیدمان عناصر داخل registerTable (کد موجود شما)
        registerTable.row().pad(10, 0, 10, 0).width(100).height(25);
        registerTable.add(randomPasswordLabel);

        registerTable.row().pad(10, 0, 10, 0).width(100).height(25);
        registerTable.add(usernameLabel).padRight(10).right();
        registerTable.add(username).left().row();

        registerTable.row().pad(10, 0, 10, 0).width(100).height(25);
        registerTable.add(passwordLabel).padRight(10).right();
        password.setPasswordMode(true); // Mask password input
        password.setPasswordCharacter('*');
        registerTable.add(password).left().row();

        registerTable.row().pad(10, 0, 10, 0).width(100).height(25);
        registerTable.add(confirmPasswordLabel).padRight(10).right();
        confirmPassword.setPasswordMode(true); // Mask confirm password input
        confirmPassword.setPasswordCharacter('*');
        registerTable.add(confirmPassword).left().row();

        registerTable.row().pad(10, 0, 10, 0).width(100).height(25);
        registerTable.add(nicknameLabel).padRight(10).right();
        registerTable.add(nickname).left().row();

        registerTable.row().pad(10, 0, 10, 0).width(100).height(25);
        registerTable.add(emailLabel).padRight(10).right();
        registerTable.add(email).left().row();

        registerTable.row().pad(10, 0, 10, 0).width(100).height(25);
        registerTable.add(genderLabel).padRight(10).right();
        registerTable.add(gender).left().row();

        // اضافه کردن دکمه‌های بازگشت و ثبت
        // فرض می‌کنم این دکمه‌ها (submitLoginButton, backToMainLoginButton,
        // submitRegisterButton, backToMainRegisterButton)
        // در جای دیگری (مثلا سازنده) مقداردهی اولیه شده‌اند.

        // این خطوط برای registerTable هستند، شما باید دکمه‌های submit و back
        // را به loginTable هم اضافه کنید.
        registerTable.row().pad(10, 0, 10, 0).width(100).height(25);
        registerTable.add(submitRegisterButton);

        registerTable.row().pad(10, 0, 10, 0).width(100).height(25);
        registerTable.add(backToMainRegisterButton);

        // **مهم**: وضعیت اولیه نمایش
        // در ابتدا فقط table اصلی (با دکمه‌های Login/Register) نمایش داده می‌شود.
        // باید مطمئن شوید که loginTable و registerTable در ابتدا مخفی هستند.
        table.setVisible(true); // مطمئن شویم که table شما قابل مشاهده است
        loginTable.setVisible(false);
        registerTable.setVisible(false);

//        // افزودن Listener ها برای دکمه‌ها
//        // این قسمت باید در سازنده کلاس یا یک متد جداگانه مثل setupListeners() قرار بگیره
//        // اما برای نمایش نحوه کارکرد، اینجا اضافه می‌کنم.
        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                table.setVisible(false); // table اصلی رو مخفی کن
                loginTable.setVisible(true); // loginTable رو نمایش بده
                registerTable.setVisible(false); // registerTable رو مخفی کن
            }
        });
//
        registerButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                table.setVisible(false); // table اصلی رو مخفی کن
                loginTable.setVisible(false); // loginTable رو مخفی کن
                registerTable.setVisible(true); // registerTable رو نمایش بده
            }
        });
//
//        // دکمه‌های بازگشت (شما باید این‌ها رو به loginTable و registerTable اضافه کنید)
//        // فرض می‌کنم backToMainLoginButton و backToMainRegisterButton قبلاً تعریف و به
//        // loginTable و registerTable اضافه شده‌اند.
//        if (backToMainLoginButton != null) {
//            backToMainLoginButton.addListener(new ClickListener() {
//                @Override
//                public void clicked(InputEvent event, float x, float y) {
//                    table.setVisible(true); // table اصلی رو نمایش بده
//                    loginTable.setVisible(false);
//                    registerTable.setVisible(false);
//                }
//            });
//        }
//
//        if (backToMainRegisterButton != null) {
//            backToMainRegisterButton.addListener(new ClickListener() {
//                @Override
//                public void clicked(InputEvent event, float x, float y) {
//                    table.setVisible(true); // table اصلی رو نمایش بده
//                    loginTable.setVisible(false);
//                    registerTable.setVisible(false);
//                }
//            });
//        }
//
//
    }

    @Override
    public void render(float v) {
        ScreenUtils.clear(0, 0, 0, 1);
        Main.getBatch().begin();
        Main.getBatch().end();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
