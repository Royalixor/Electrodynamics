package electrodynamics.recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

public class RecipeManagerSinteringOven {

	public static final int DEFAULT_PROCESSING_TIME = 10 * 20; // 10 seconds.

	public ArrayList<RecipeSinteringOven> sinteringOvenRecipes = new ArrayList<RecipeSinteringOven>();
	
	public void registerRecipe(RecipeSinteringOven recipe) {
		sinteringOvenRecipes.add(recipe);
	}
	
	public void registerRecipe(List<ItemStack> input, List<ItemStack> output, int duration) {
		this.registerRecipe(new RecipeSinteringOven(input, output, duration));
	}
	
	public RecipeSinteringOven getRecipe(List<ItemStack> input) {
		RecipeSinteringOven recipe = getOvenRecipe(input);
		
		if (recipe == null) {
			input = trimItemStackList(input);
			int processingTime = 0;
			List<ItemStack> realInput = new ArrayList<ItemStack>(), output = new ArrayList<ItemStack>();
			for( ItemStack item : input ) {
				recipe = getFurnaceRecipe( item );
				if( recipe != null ) {
					realInput.add( item );
					output.addAll( recipe.itemOutputs );
					processingTime += recipe.processingTime;
				}
			}
			recipe = new RecipeSinteringOven( realInput, output, processingTime );
		}
		
		return recipe;
	}
	
	public RecipeSinteringOven getOvenRecipe(List<ItemStack> input) {
		if (input == null) return null;
		
		for (RecipeSinteringOven recipe : sinteringOvenRecipes) {
			if (recipe.isInput(input)) {
				return recipe;
			}
		}
		
		return null;
	}
	
	public RecipeSinteringOven getFurnaceRecipe(ItemStack stack) {
		if (stack == null) return null;

		ItemStack result = FurnaceRecipes.smelting().getSmeltingResult( stack );
		if( result != null ) {
			return new RecipeSinteringOven( Arrays.asList(stack), Arrays.asList( result ), DEFAULT_PROCESSING_TIME );
		}
		
		return null;
	}
	
	public List<ItemStack> trimItemStackList(List<ItemStack> input) {
		List<ItemStack> inputs = new ArrayList<ItemStack>();
		
		for (ItemStack stack : input) {
			if (stack != null) {
				inputs.add(stack);
			}
		}
		
		return inputs;
	}
	
	public void initRecipes() {
		
	}
	
}
