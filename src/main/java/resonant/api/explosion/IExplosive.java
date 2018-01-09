package resonant.api.explosion;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import resonant.api.ITier;

/**
 * An interface used to find various types of explosive's information.
 *
 * @author Calclavia
 */
public interface IExplosive extends ITier
{
    /** @return The unique name key in the ICBM language file. */
    public String getUnlocalizedName();

    /** @return Gets the specific translated name of the block versions of the explosive. */
    public String getExplosiveName();

    /** @return Gets the specific translated name of the grenade versions of the explosive. */
    public String getGrenadeName();

    /** @return Gets the specific translated name of the missile versions of the explosive. */
    public String getMissileName();

    /** @return Gets the specific translated name of the minecart versions of the explosive. */
    public String getMinecartName();

    /** @return The tier of the explosive. */
    @Override
    public int getTier();

    /**
     * Creates a new explosion at a given location.
     *
     * @param world  The world in which the explosion takes place.
     * @param pos
     * @param entity Entity that caused the explosion.
     */
    public void createExplosion(World world, BlockPos pos, Entity entity);

}