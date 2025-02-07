package api.models.users;

import api.models.asset.Asset;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class User {

    private final String userName;
    private final String email;
    private final String password;
    private double deposit;
    private final Map<String, Asset> wallet;

    public User(String userName, String email, String password) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String bCryptPassword = bCryptPasswordEncoder.encode(password);

        this.userName = userName;
        this.email = email;
        this.password = bCryptPassword;

        this.deposit = 0;
        this.wallet = new LinkedHashMap<>();
    }

    public String getUserName() {
        return this.userName;
    }

    public String getEmail() {
        return this.email;
    }

    public double getDeposit() {
        return this.deposit;
    }

    public Map<String, Asset> getWallet() {
        return this.wallet;
    }

    public boolean isPasswordCorrect(String plainPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(plainPassword, this.password);
    }

    public Asset getAsset(String assetID) {
        return this.wallet.get(assetID);
    }

    public void buyAsset(String assetID, String name, double priceUSD, double amount) {
        Asset existingAsset = this.wallet.get(assetID);

        if (existingAsset != null) {
            double newAmount = existingAsset.amount() + amount;
            this.wallet.put(assetID, new Asset(assetID, name, priceUSD, existingAsset.isCrypto(), newAmount));
        } else {
            this.wallet.put(assetID, new Asset(assetID, name, priceUSD, 1, amount));
        }
    }

    public void sellAsset(String assetID) {
        this.wallet.remove(assetID);
    }

    public void depositMoney(double deposit) {
        this.deposit += deposit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userName, user.userName) &&
            Objects.equals(email, user.email) &&
            Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, email, password);
    }
}
