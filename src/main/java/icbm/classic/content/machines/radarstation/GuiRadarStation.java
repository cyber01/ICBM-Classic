package icbm.classic.content.machines.radarstation;

import icbm.classic.ICBMClassic;
import icbm.classic.lib.LanguageUtility;
import icbm.classic.lib.network.packet.PacketTile;
import icbm.classic.lib.transform.region.Rectangle;
import icbm.classic.lib.transform.vector.Point;
import icbm.classic.lib.transform.vector.Pos;
import icbm.classic.prefab.gui.GuiContainerBase;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class GuiRadarStation extends GuiContainerBase
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(ICBMClassic.DOMAIN, ICBMClassic.GUI_DIRECTORY + "gui_radar.png");
    public static final ResourceLocation TEXTURE_RED_DOT = new ResourceLocation(ICBMClassic.DOMAIN, ICBMClassic.GUI_DIRECTORY + "reddot.png");
    public static final ResourceLocation TEXTURE_YELLOW_DOT = new ResourceLocation(ICBMClassic.DOMAIN, ICBMClassic.GUI_DIRECTORY + "yellowdot.png");
    public static final ResourceLocation TEXTURE_WHITE_DOT = new ResourceLocation(ICBMClassic.DOMAIN, ICBMClassic.GUI_DIRECTORY + "whitedot.png");
    private TileRadarStation tileEntity;

    private int containerPosX;
    private int containerPosY;

    private GuiTextField textFieldAlarmRange;
    private GuiTextField textFieldSafetyZone;
    private GuiTextField textFieldFrequency;

    private Point mouseOverCoords = new Point();
    private Point mousePosition = new Point();

    // Radar Map
    private Point radarCenter;
    private float radarMapRadius;

    private String info = "";

    private String info2;

    public GuiRadarStation(EntityPlayer player, TileRadarStation tileEntity)
    {
        super(new ContainerRadarStation(player, tileEntity));
        this.tileEntity = tileEntity;
        mouseOverCoords = new Point(this.tileEntity.getPos().getX(), this.tileEntity.getPos().getZ());
        ySize = 166;
        xSize = 256;
        radarCenter = new Point(this.containerPosX + this.xSize / 3 - 14, this.containerPosY + this.ySize / 2 + 4);
        radarMapRadius = TileRadarStation.MAX_DETECTION_RANGE / 63.8F;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        this.textFieldSafetyZone = new GuiTextField(0, fontRenderer, 210, 67, 30, 12);
        this.textFieldSafetyZone.setMaxStringLength(3);
        this.textFieldSafetyZone.setText(this.tileEntity.safetyRange + "");

        this.textFieldAlarmRange = new GuiTextField(1, fontRenderer, 210, 82, 30, 12);
        this.textFieldAlarmRange.setMaxStringLength(3);
        this.textFieldAlarmRange.setText(this.tileEntity.alarmRange + "");

        this.textFieldFrequency = new GuiTextField(2, fontRenderer, 155, 112, 50, 12);
        this.textFieldFrequency.setMaxStringLength(6);
        this.textFieldFrequency.setText(this.tileEntity.getFrequency() + "");

        //Engine.instance.packetHandler.sendToServer(new PacketTile(this.tileEntity, -1, true));
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();
        //Engine.instance.packetHandler.sendToServer(new PacketTile(this.tileEntity, -1, false));
    }

    /** Draw the foreground layer for the GuiContainer (everything in front of the items) */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRenderer.drawString("\u00a77" + LanguageUtility.getLocal(ICBMClassic.blockRadarStation.getUnlocalizedName()), this.xSize / 2 - 30, 6, 4210752);

        this.fontRenderer.drawString(LanguageUtility.getLocal("gui.radar.coords"), 155, 18, 4210752);
        this.fontRenderer.drawString(LanguageUtility.getLocal("gui.misc.x") + " " + (int) Math.round(mouseOverCoords.x()) + " " + LanguageUtility.getLocal("gui.misc.z") + " " + (int) Math.round(mouseOverCoords.y()), 155, 30, 4210752);

        this.fontRenderer.drawString("\u00a76" + this.info, 155, 42, 4210752);
        this.fontRenderer.drawString("\u00a74" + this.info2, 155, 54, 4210752);

        this.fontRenderer.drawString(LanguageUtility.getLocal("gui.radar.zoneSafe"), 152, 70, 4210752);
        this.textFieldSafetyZone.drawTextBox();
        this.fontRenderer.drawString(LanguageUtility.getLocal("gui.radar.zoneAlarm"), 150, 85, 4210752);
        this.textFieldAlarmRange.drawTextBox();

        this.fontRenderer.drawString(LanguageUtility.getLocal("gui.misc.freq"), 155, 100, 4210752);
        this.textFieldFrequency.drawTextBox();

        //this.fontRenderer.drawString(UnitDisplay.getDisplay(TileRadarStation.WATTS, UnitDisplay.Unit.WATT), 155, 128, 4210752);

        //this.fontRenderer.drawString(UnitDisplay.getDisplay(this.tileEntity.getVoltageInput(null), Unit.VOLTAGE), 155, 138, 4210752);

        // Shows the status of the radar
        String color = "\u00a74";
        String status = LanguageUtility.getLocal("gui.misc.idle");

        if (this.tileEntity.hasPower())
        {
            color = "\u00a72";
            status = LanguageUtility.getLocal("gui.radar.on");
        }
        else
        {
            status = LanguageUtility.getLocal("gui.radar.nopower");
        }

        this.fontRenderer.drawString(color + status, 155, 150, 4210752);
    }

    /** Call this method from you GuiScreen to process the keys into textbox. */
    @Override
    public void keyTyped(char par1, int par2) throws IOException
    {
        super.keyTyped(par1, par2);
        this.textFieldSafetyZone.textboxKeyTyped(par1, par2);
        this.textFieldAlarmRange.textboxKeyTyped(par1, par2);
        this.textFieldFrequency.textboxKeyTyped(par1, par2);

        try
        {
            int newSafetyRadius = Math.min(TileRadarStation.MAX_DETECTION_RANGE, Math.max(0, Integer.parseInt(this.textFieldSafetyZone.getText())));
            this.tileEntity.safetyRange = newSafetyRadius;
            ICBMClassic.packetHandler.sendToServer(new PacketTile("safeRange_C>S", 2, this.tileEntity).addData(this.tileEntity.safetyRange));
        }
        catch (NumberFormatException e)
        {
        }

        try
        {
            int newAlarmRadius = Math.min(TileRadarStation.MAX_DETECTION_RANGE, Math.max(0, Integer.parseInt(this.textFieldAlarmRange.getText())));
            this.tileEntity.alarmRange = newAlarmRadius;
            ICBMClassic.packetHandler.sendToServer(new PacketTile("alarmRange_C>S", 3, this.tileEntity).addData(this.tileEntity.alarmRange));
        }
        catch (NumberFormatException e)
        {
        }

        try
        {
            this.tileEntity.setFrequency(Integer.parseInt(this.textFieldFrequency.getText()));
            ICBMClassic.packetHandler.sendToServer(new PacketTile("frequency_C>S", 4, this.tileEntity).addData(this.tileEntity.getFrequency()));
        }
        catch (NumberFormatException e)
        {
        }

    }

    /** Args: x, y, buttonClicked */
    @Override
    public void mouseClicked(int par1, int par2, int par3) throws IOException
    {
        super.mouseClicked(par1, par2, par3);
        this.textFieldAlarmRange.mouseClicked(par1 - containerPosX, par2 - containerPosY, par3);
        this.textFieldSafetyZone.mouseClicked(par1 - containerPosX, par2 - containerPosY, par3);
        this.textFieldFrequency.mouseClicked(par1 - containerPosX, par2 - containerPosY, par3);
    }

    /** Draw the background layer for the GuiContainer (everything behind the items) */
    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int mouseX, int mouseY)
    {
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.containerPosX = (this.width - this.xSize) / 2;
        this.containerPosY = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(containerPosX, containerPosY, 0, 0, this.xSize, this.ySize);

        this.radarCenter = new Point(this.containerPosX + this.xSize / 3 - 10, this.containerPosY + this.ySize / 2 + 4);
        this.radarMapRadius = TileRadarStation.MAX_DETECTION_RANGE / 71f;

        this.info = "";
        this.info2 = "";

        if (this.tileEntity.hasPower())
        {
            int range = 4;

            for (Pos pos : tileEntity.guiDrawPoints)
            {
                final int type = (int) pos.z();
                final double x = pos.x();
                final double z = pos.y();

                Point position = new Point(radarCenter.x() + (x - this.tileEntity.getPos().getX()) / this.radarMapRadius, radarCenter.y() - (z - this.tileEntity.getPos().getZ()) / this.radarMapRadius);

                switch (type)
                {
                    case 1:
                        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE_YELLOW_DOT);
                        break;
                    case 2:
                        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE_RED_DOT);
                        break;
                    case 0:
                    default:
                        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE_WHITE_DOT);
                        break;
                }

                this.drawTexturedModalRect(position.xi(), position.yi(), 0, 0, 2, 2);

                // Hover Detection
                Point minPosition = position.add(-range);
                Point maxPosition = position.add(range);

                if (new Rectangle(minPosition, maxPosition).isWithin(this.mousePosition))
                {
                    if (type == 0)
                    {
                        this.info = "Object: (" + x + "x, " + z + "z)";
                    }
                    else
                    {
                        this.info = "Missile: (" + x + "x, " + z + "z)";
                    }
                }
            }
        }
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        if (Mouse.isInsideWindow())
        {
            if (Mouse.getEventButton() == -1)
            {
                this.mousePosition = new Point(Mouse.getEventX() * this.width / this.mc.displayWidth, this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1);

                float difference = TileRadarStation.MAX_DETECTION_RANGE / this.radarMapRadius;

                if (this.mousePosition.x() > this.radarCenter.x() - difference && this.mousePosition.x() < this.radarCenter.x() + difference && this.mousePosition.y() > this.radarCenter.y() - difference && this.mousePosition.y() < this.radarCenter.y() + difference)
                {
                    // Calculate from the mouse position the relative position
                    // on the grid
                    int xDifference = (int) (this.mousePosition.x() - this.radarCenter.x());
                    int yDifference = (int) (this.mousePosition.y() - this.radarCenter.y());
                    int xBlockDistance = (int) (xDifference * this.radarMapRadius);
                    int yBlockDistance = (int) (yDifference * this.radarMapRadius);

                    this.mouseOverCoords = new Point(this.tileEntity.getPos().getX() + xBlockDistance, this.tileEntity.getPos().getZ() - yBlockDistance);
                }
            }
        }

        if (!this.textFieldSafetyZone.isFocused())
        {
            this.textFieldSafetyZone.setText(this.tileEntity.safetyRange + "");
        }
        if (!this.textFieldAlarmRange.isFocused())
        {
            this.textFieldAlarmRange.setText(this.tileEntity.alarmRange + "");
        }
    }
}
