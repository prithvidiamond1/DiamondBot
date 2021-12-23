package org.prithvidiamond1.SlashCommands.Customizers;

import org.javacord.api.interaction.SlashCommandOption;
import org.javacord.api.interaction.SlashCommandOptionChoice;
import org.javacord.api.interaction.SlashCommandOptionType;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface that allows for the customization of slash commands using options
 */
public abstract class SlashCommandOptionCustomizer {
    /**
     * List to contain all the slash command option customizations for a slash command
     */
    List<SlashCommandOption> optionList;

    /**
     * Constructor of this abstract class
     */
    public SlashCommandOptionCustomizer(){
        this.optionList = new ArrayList<>();
    }

    /**
     * Method to add an option to the slash command
     * @param optionType the type of the option
     * @param optionName the name of the option
     * @param optionDesc the description of the option
     * @param required if the option is required or not to run the slash command
     */
    public void addCommandOption(SlashCommandOptionType optionType,
                                         String optionName,
                                         String optionDesc,
                                         boolean required) {
        SlashCommandOption option = SlashCommandOption.create(
                optionType,
                optionName,
                optionDesc,
                required
        );
        this.optionList.add(option);
    }

    /**
     * Method to add an option with option choices to the slash command
     * @param optionType the type of the option
     * @param optionName the name of the option
     * @param optionDesc the description of the option
     * @param required if the option is required or not to run the slash command
     * @param optionChoices the choices for the option
     */
    public void addCommandOptionWithChoices(SlashCommandOptionType optionType,
                                                    String optionName,
                                                    String optionDesc,
                                                    boolean required,
                                                    List<SlashCommandOptionChoice> optionChoices) {
        SlashCommandOption option = SlashCommandOption.createWithChoices(
                optionType,
                optionName,
                optionDesc,
                required,
                optionChoices
        );
        this.optionList.add(option);
    }

    /**
     * Method that returns the list of options added to the slash command
     * @return a list of the options added to the slash command
     */
    public List<SlashCommandOption> getOptionList() {
        return this.optionList;
    }
}
