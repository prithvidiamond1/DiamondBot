package org.prithvidiamond1.SlashCommandCustomizers;

import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface that allows for the customization of slash commands using sub commands
 * <br>
 * Inherits {@link SlashCommandOptionCustomizer}
 */
public abstract class SlashCommandSubCommandCustomizer extends SlashCommandOptionCustomizer {
    /**
     * List to contain all the slash command sub command customizations for a slash command
     */
    List<SlashCommandOption> subCommandList;

    /**
     * Constructor of this abstract class
     */
    public SlashCommandSubCommandCustomizer(){
        this.subCommandList = new ArrayList<>();
    }

    /**
     * Method to add a sub command to the slash command
     * @param subCommandName the name of the sub command
     * @param subCommandDesc the description of the sub command
     */
    public void addSubCommand(String subCommandName,
                                      String subCommandDesc) {
        SlashCommandOption subCommand = SlashCommandOption.createWithOptions(
                SlashCommandOptionType.SUB_COMMAND,
                subCommandName,
                subCommandDesc,
                this.getOptionList()
        );
        this.subCommandList.add(subCommand);
    }

    /**
     * Method that returns the list of sub commands added to the slash command
     * @return a list of the sub commands added to the slash command
     */
    public List<SlashCommandOption> getSubCommandList() {
        return this.subCommandList;
    }
}
