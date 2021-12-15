package org.prithvidiamond1.SlashCommands.Customizers;

import org.javacord.api.interaction.SlashCommandBuilder;

/**
 * This class contains methods to customize slash commands by implementing {@link SlashCommandCustomizerInterface}
 */
public class SlashCommandCustomizer implements SlashCommandCustomizerInterface {
    private final SlashCommandBuilder command;

    /**
     * Constructor for the customizer
     * @param command the slash command to be customized
     */
    public SlashCommandCustomizer(SlashCommandBuilder command){
        this.command = command;
    }

    /**
     * Method to save the customizations done to the slash command
     * @return the customized slash command builder
     */
    public SlashCommandBuilder setCustomizations(){
        if (this.getSubCommandGroupList().isEmpty()){
            if (this.getSubCommandList().isEmpty()){
                if (this.getOptionList().isEmpty()){
                    // Option-less slash command handling perhaps? -> not required
                    System.out.println("No options set for this slash command");
                }
                else {
                    this.command.setOptions(this.getOptionList());
                }
            }
            else{
                this.command.setOptions(this.getSubCommandList());
            }
        }
        else{
            this.command.setOptions(this.getSubCommandGroupList());
        }
        return this.command;
    }

}

