package org.prithvidiamond1.SlashCommandCustomizers;

import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionType;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface that allows for the customization of slash commands using sub command groups
 * <br>
 * Inherits {@link SlashCommandSubCommandCustomizer}
 */
public abstract class SlashCommandSubCommandGroupCustomizer extends SlashCommandSubCommandCustomizer {
    /**
     * List to contain all the slash command sub command group customizations for a slash command
     */
    List<SlashCommandOption> subCommandGroupList;

    /**
     * Constructor of this abstract class
     */
    public SlashCommandSubCommandGroupCustomizer(){
        this.subCommandGroupList = new ArrayList<>();
    }

    /**
     * Method to add a sub command group to the slash command
     * @param subCommandGroupName the name of the sub command group
     * @param subCommandGroupDesc the description of the sub command group
     */
    public void addSubCommandGroup(String subCommandGroupName,
                                    String subCommandGroupDesc) {
        SlashCommandOption subCommandGroup = SlashCommandOption.createWithOptions(
                SlashCommandOptionType.SUB_COMMAND_GROUP,
                subCommandGroupName,
                subCommandGroupDesc,
                this.getSubCommandList()
        );
        this.subCommandGroupList.add(subCommandGroup);
    }

    /**
     * Method that returns the list of sub command groups added to the slash command
     * @return a list of the sub command groups added to the slash command
     */
    public List<SlashCommandOption> getSubCommandGroupList() {
        return this.subCommandGroupList;
    }

}
