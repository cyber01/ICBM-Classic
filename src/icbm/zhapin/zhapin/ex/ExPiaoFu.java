package icbm.zhapin.zhapin.ex;

import icbm.core.SheDing;
import icbm.core.di.MICBM;
import icbm.zhapin.baozha.bz.BzPiaoFu;
import icbm.zhapin.muoxing.daodan.MMPiaoFu;
import icbm.zhapin.zhapin.daodan.DaoDan;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import universalelectricity.prefab.RecipeHelper;

public class ExPiaoFu extends DaoDan
{
	public ExPiaoFu(String mingZi, int tier)
	{
		super(mingZi, tier);
	}

	@Override
	public void init()
	{
		RecipeHelper.addRecipe(new ShapedOreRecipe(this.getItemStack(), new Object[] { "EEE", "ETE", "EEE", 'T', tui.getItemStack(), 'E', Item.eyeOfEnder }), this.getUnlocalizedName(), SheDing.CONFIGURATION, true);
	}

	@Override
	public void createExplosion(World world, double x, double y, double z, Entity entity)
	{
		new BzPiaoFu(world, entity, x, y, z, 30).explode();
	}

	@Override
	public MICBM getMuoXing()
	{
		return new MMPiaoFu();
	}
}
