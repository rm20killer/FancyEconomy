package de.oliver.fancyeconomy.commands;

import de.oliver.fancyeconomy.FancyEconomy;
import de.oliver.fancyeconomy.currencies.BalanceTop;
import de.oliver.fancyeconomy.currencies.Currency;
import de.oliver.fancyeconomy.currencies.CurrencyPlayer;
import de.oliver.fancyeconomy.currencies.CurrencyPlayerManager;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancylib.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class CurrencyBaseCMD {

    private final Currency currency;

    public CurrencyBaseCMD(Currency currency) {
        this.currency = currency;
    }

    public void info(Player player) {
        MessageHelper.info(player, " --- FancyEconomy Info ---");
        MessageHelper.info(player, "/" + currency.name() + " balance - Shows your balance");
        MessageHelper.info(player, "/" + currency.name() + " balance <player> - Shows a player's balance");
        MessageHelper.info(player, "/" + currency.name() + " top <page> - Shows the richest players");
        MessageHelper.info(player, "/" + currency.name() + " set <player> <amount> - Sets the balance of a certain player");
        MessageHelper.info(player, "/" + currency.name() + " add <player> <amount> - Adds money to a certain player");
        MessageHelper.info(player, "/" + currency.name() + " remove <player> <amount> - Removes money to a certain player");
    }

    public void balance(Player player) {
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(player.getUniqueId());
        double balance = currencyPlayer.getBalance(currency);

        MessageHelper.info(player, FancyEconomy.getInstance().getLang().get(
                "your-balance",
                "balance", currency.format(balance)
        ));
    }

    public void balance(
            Player player,
            String targetName
    ) {
        Player targetPlayer = Bukkit.getPlayer(targetName);
        if (targetPlayer != null) {
            targetName = targetPlayer.getName();
        }

        UUID uuid = targetPlayer != null ? targetPlayer.getUniqueId() : UUIDFetcher.getUUID(targetName);

        if (uuid == null) {
            MessageHelper.error(player, FancyEconomy.getInstance().getLang().get(
                    "player-not-found",
                    "player", targetName
            ));
            return;
        }

        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(uuid);

        if (targetPlayer != null) {
            currencyPlayer.setUsername(targetPlayer.getName());
        }

        double balance = currencyPlayer.getBalance(currency);

        MessageHelper.info(player, FancyEconomy.getInstance().getLang().get(
                "balance-others",
                "player", currencyPlayer.getUsername(),
                "balance", currency.format(balance)
        ));
    }


    public void balancetop(Player player) {
        balancetop(player, 1);
    }

    public void balancetop(
            Player player,
            int page
    ) {
        BalanceTop balanceTop = BalanceTop.getForCurrency(currency);

        if ((page - 1) * BalanceTopCMD.ENTRIES_PER_PAGE > balanceTop.getAmountEntries()) {
            MessageHelper.warning(player, FancyEconomy.getInstance().getLang().get("balance-top-empty-page"));
            return;
        }

        MessageHelper.info(player, "<b>Balance top: " + currency.name() + "</b> <gray>(Page #" + page + ")");

        for (int i = 1; i <= BalanceTopCMD.ENTRIES_PER_PAGE; i++) {
            final int place = (page - 1) * BalanceTopCMD.ENTRIES_PER_PAGE + i;
            UUID uuid = balanceTop.getAtPlace(place);
            if (uuid == null) {
                break;
            }

            CurrencyPlayer cp = CurrencyPlayerManager.getPlayer(uuid);
            MessageHelper.info(player, place + ". " + cp.getUsername() + " <gray>(" + currency.format(cp.getBalance(currency)) + ")");
        }

        int yourPlace = balanceTop.getPlayerPlace(player.getUniqueId());
        MessageHelper.info(player, FancyEconomy.getInstance().getLang().get(
                "balancetop-your-place",
                "place", yourPlace > 0 ? String.valueOf(yourPlace) : "N/A"
        ));
    }

    public void set(
            Player player,
            String targetName,
            double amount
    ) {
        Player targetPlayer = Bukkit.getPlayer(targetName);
        if (targetPlayer != null) {
            targetName = targetPlayer.getName();
        }

        UUID uuid = targetPlayer != null ? targetPlayer.getUniqueId() : UUIDFetcher.getUUID(targetName);

        if (uuid == null) {
            MessageHelper.error(player, FancyEconomy.getInstance().getLang().get(
                    "player-not-found",
                    "player", targetName
            ));
            return;
        }

        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(uuid);
        currencyPlayer.setBalance(currency, amount);

        MessageHelper.success(player, FancyEconomy.getInstance().getLang().get(
                "set-success",
                "player", currencyPlayer.getUsername(),
                "amount", currency.format(amount)
        ));
    }

    public void add(
            Player player,
            String targetName,
            double amount
    ) {
        Player targetPlayer = Bukkit.getPlayer(targetName);
        if (targetPlayer != null) {
            targetName = targetPlayer.getName();
        }

        UUID uuid = targetPlayer != null ? targetPlayer.getUniqueId() : UUIDFetcher.getUUID(targetName);

        if (uuid == null) {
            MessageHelper.error(player, FancyEconomy.getInstance().getLang().get(
                    "player-not-found",
                    "player", targetName
            ));
            return;
        }

        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(uuid);
        currencyPlayer.addBalance(currency, amount);

        MessageHelper.success(player, FancyEconomy.getInstance().getLang().get(
                "add-success",
                "player", currencyPlayer.getUsername(),
                "amount", currency.format(amount)
        ));
    }

    public void remove(
            Player player,
            String targetName,
            double amount
    ) {
        Player targetPlayer = Bukkit.getPlayer(targetName);
        if (targetPlayer != null) {
            targetName = targetPlayer.getName();
        }

        UUID uuid = targetPlayer != null ? targetPlayer.getUniqueId() : UUIDFetcher.getUUID(targetName);

        if (uuid == null) {
            MessageHelper.error(player, FancyEconomy.getInstance().getLang().get(
                    "player-not-found",
                    "player", targetName
            ));
            return;
        }

        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(uuid);
        currencyPlayer.removeBalance(currency, amount);

        MessageHelper.success(player, FancyEconomy.getInstance().getLang().get(
                "remove-success",
                "player", currencyPlayer.getUsername(),
                "amount", currency.format(amount)
        ));
    }
}
