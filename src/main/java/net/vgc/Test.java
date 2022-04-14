package net.vgc;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import net.vgc.data.json.JsonHelper;
import net.vgc.language.Language;
import net.vgc.language.LanguageFile;
import net.vgc.language.Languages;
import net.vgc.language.Translation;
import net.vgc.util.Util;

public class Test {
	
	protected static final Logger LOGGER = LogManager.getLogger();
	
	public static void main(String[] args) {
		makeLanguageFiles();
	}
	
	protected static void makeLanguageFiles() {
		List<Translation> english = Util.make(Lists.newArrayList(), (list) -> {
			list.add(new Translation("client.constans.name", "Virtual Game Collection"));
			list.add(new Translation("screen.loading.title", "Loading Virtual Game Collection"));
			list.add(new Translation("screen.loading.loading.text", "Loading %1%%"));
			list.add(new Translation("screen.menu.singleplayer", "Singleplayer"));
			list.add(new Translation("screen.menu.multiplayer", "Multiplayer"));
			list.add(new Translation("screen.menu.settings", "Settings"));
			list.add(new Translation("screen.menu.login", "Login"));
			list.add(new Translation("screen.menu.profile", "Profile"));
			list.add(new Translation("window.login.register", "Register"));
			list.add(new Translation("window.login.username", "Username"));
			list.add(new Translation("window.login.password", "Password"));
			list.add(new Translation("window.login.confirm_password", "Confirm Password"));
			list.add(new Translation("window.login.login", "Login"));
			list.add(new Translation("window.login.guest", "Guest"));
			list.add(new Translation("window.login.name", "Name"));
			list.add(new Translation("window.login.user", "User"));
			list.add(new Translation("window.login.back", "Back"));
			list.add(new Translation("window.logout.logout", "Logout"));
			list.add(new Translation("account.login.create", "Account has been created"));
			list.add(new Translation("account.login.guest", "Successfully logged in as a guest"));
			list.add(new Translation("account.login.unknown", "Unknown login type. Please try again later"));
			list.add(new Translation("account.login.no", "No Account found with matching account data"));
			list.add(new Translation("account.login.taken", "Account is already used by another player"));
			list.add(new Translation("account.login.successfully", "Successfully logged in"));
			list.add(new Translation("account.logout.unused", "The Account which should be logged out is not used by a player"));
			list.add(new Translation("account.logout.successfully", "Successfully logged out"));
			list.add(new Translation("account.constans.name", "Account Server"));
			list.add(new Translation("account.window.accounts", "Accounts"));
			list.add(new Translation("account.window.account", "%1%'s Account"));
			list.add(new Translation("account.window.account_name", "Name: %1%"));
			list.add(new Translation("account.window.account_password", "Password: %1%"));
			list.add(new Translation("account.window.account_uuid", "Id: %1%"));
			list.add(new Translation("account.window.account_guest", "Guest: %1%"));
			list.add(new Translation("account.window.account_taken", "Taken: %1%"));
			list.add(new Translation("account.window.create", "Create account"));
			list.add(new Translation("account.window.remove", "Remove account"));
			list.add(new Translation("account.window.refresh", "Refresh"));
			list.add(new Translation("account.window.close", "Close"));
			list.add(new Translation("window.create_account.true", "Yes"));
			list.add(new Translation("window.create_account.false", "No"));
		});
		List<Translation> german = Util.make(Lists.newArrayList(), (list) -> {
			list.add(new Translation("client.constans.name", "Virtuelle Spielesammlung"));
			list.add(new Translation("screen.loading.title", "Virtuelle Spielesammlung wird geladen"));
			list.add(new Translation("screen.loading.loading.text", "Wird geladen %1%%"));
			list.add(new Translation("screen.menu.singleplayer", "Einzelspieler"));
			list.add(new Translation("screen.menu.multiplayer", "Mehrspieler"));
			list.add(new Translation("screen.menu.settings", "Einstellungen"));
			list.add(new Translation("screen.menu.login", "Anmeldung"));
			list.add(new Translation("screen.menu.profile", "Profil"));
			list.add(new Translation("window.login.register", "Registrieren"));
			list.add(new Translation("window.login.username", "Nutzername"));
			list.add(new Translation("window.login.password", "Passwort"));
			list.add(new Translation("window.login.confirm_password", "Passwort bestätigen"));
			list.add(new Translation("window.login.login", "Anmelden"));
			list.add(new Translation("window.login.guest", "Gast"));
			list.add(new Translation("window.login.name", "Name"));
			list.add(new Translation("window.login.user", "Benutzer"));
			list.add(new Translation("window.login.back", "Zurück"));
			list.add(new Translation("window.logout.logout", "Abmelden"));
			list.add(new Translation("account.login.create", "Konto wurde erstellt"));
			list.add(new Translation("account.login.guest", "Als Gast erfolgreich eingeloggt"));
			list.add(new Translation("account.login.unknown", "Unbekannter Anmeldetyp. Bitte versuchen Sie es später erneut"));
			list.add(new Translation("account.login.no", "Kein Konto mit übereinstimmenden Kontodaten gefunden"));
			list.add(new Translation("account.login.taken", "Konto wird bereits von einem anderen Spieler verwendet"));
			list.add(new Translation("account.login.successfully", "Erfolgreich eingeloggt"));
			list.add(new Translation("account.logout.unused", "Das abzumeldende Konto wird von keinem Spieler verwendet"));
			list.add(new Translation("account.logout.successfully", "Erfolgreich abgemeldet"));
			list.add(new Translation("account.constans.name", "Account Server"));
			list.add(new Translation("account.window.accounts", "Konten"));
			list.add(new Translation("account.window.account", "%1%'s Konto"));
			list.add(new Translation("account.window.account_name", "Name: %1%"));
			list.add(new Translation("account.window.account_password", "Passwort: %1%"));
			list.add(new Translation("account.window.account_uuid", "Id: %1%"));
			list.add(new Translation("account.window.account_guest", "Gast: %1%"));
			list.add(new Translation("account.window.account_taken", "Verwendet: %1%"));
			list.add(new Translation("account.window.create", "Konto erstellen"));
			list.add(new Translation("account.window.remove", "Konto löschen"));
			list.add(new Translation("account.window.refresh", "Aktualisieren"));
			list.add(new Translation("account.window.close", "Schließen"));
			list.add(new Translation("window.create_account.true", "Ja"));
			list.add(new Translation("window.create_account.false", "Nein"));
		});
		saveLanguageFile(Languages.EN_US, english);
		saveLanguageFile(Languages.DE_DE, german);
	}
	
	protected static void saveLanguageFile(Language language, List<Translation> translations) {
		LanguageFile languageFile = new LanguageFile(language, translations);
		Function<LanguageFile, DataResult<JsonElement>> function = JsonOps.INSTANCE.withEncoder(LanguageFile.CODEC);
		Optional<JsonElement> optional = function.apply(languageFile).result();
		if (optional.isPresent()) {
			JsonHelper.save(new GsonBuilder().setPrettyPrinting().create(), optional.get(), new File(System.getProperty("user.home")).toPath().resolve("Desktop/" + language.getFileName() + ".json"));
		}
	}

}
