package electrodynamics.api.crafting;

import electrodynamics.recipe.RecipeBasicSieve;

public abstract interface ISieveManager {

	public abstract void registerSieveRecipe(RecipeBasicSieve recipe);
	
}