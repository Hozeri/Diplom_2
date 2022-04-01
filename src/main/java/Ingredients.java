import io.restassured.response.Response;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;

public class Ingredients {

    public final List<Object> ingredients;

    public Ingredients(List<Object> ingredients) {
        this.ingredients = ingredients;
    }

    public static Ingredients getRandomIngredientsForBurger() {
        OrderClient orderClient = new OrderClient();
        Response response = orderClient.getIngredients();

        List<Object> buns = response.jsonPath().getList("data.findAll {it.type == 'bun'}._id");
        List<Object> sauces = response.jsonPath().getList("data.findAll {it.type == 'sauce'}._id");
        List<Object> mains = response.jsonPath().getList("data.findAll {it.type == 'main'}._id");

        Object bun = buns.get(RandomUtils.nextInt(0,2));
        Object sauce = sauces.get(RandomUtils.nextInt(0,4));
        Object main = mains.get(RandomUtils.nextInt(0,9));

        List<Object> ingredients = List.of(bun, sauce, main);

        return new Ingredients(ingredients);
    }

    public static Ingredients getBurgerWithoutIngredients() {
        List<Object> ingredients = List.of();
        return new Ingredients(ingredients);
    }

    public static Ingredients getIngredientsWithWrongHash() {
        List<Object> ingredients = List.of("61c0c5a71d1f82001bdaaa71Q",
                                           "61c0c5a71d1f82001bdaaa71W",
                                           "61c0c5a71d1f82001bdaaa71E");
        return new Ingredients(ingredients);
    }

}
