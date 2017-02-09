/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aesh.command.impl.parser;

import org.aesh.command.impl.internal.ProcessedOption;
import org.aesh.command.invocation.InvocationProviders;
import org.aesh.command.populator.CommandPopulator;
import org.aesh.command.impl.internal.ProcessedCommand;
import org.aesh.command.Command;
import org.aesh.command.validator.OptionValidatorException;
import org.aesh.console.AeshContext;
import org.aesh.parser.ParsedLineIterator;

import java.util.List;

/**
 * A command line parser that is created based on a given
 * ProcessedCommand.
 *
 * It must also be able to inject values from a line into a Command object
 *
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public interface CommandLineParser<C extends Command> {

    /**
     * @return the processed command this parser is generated from
     */
    ProcessedCommand<C> getProcessedCommand();

    /**
     * @return the actual command
     */
    C getCommand();

    /**
     * @return completion parser created to work on this command
     */
    CommandLineCompletionParser getCompletionParser();

    List<String> getAllNames();

    /**
     * @param name command
     * @return child parser that matches the name
     */
    CommandLineParser<C> getChildParser(String name);

    void addChildParser(CommandLineParser<C> childParser);

    /**
     * @return all the child parser
     */
    List<CommandLineParser<C>> getAllChildParsers();

    /**
     * @return command populator to work on this command
     */
    CommandPopulator<Object, C> getCommandPopulator();


    /**
     * Direct call to CommandPopulator to populate this command
     *
     * @param invocationProviders providers
     * @param aeshContext context
     * @param validate validate
     * @throws CommandLineParserException parser exception
     * @throws OptionValidatorException validator exception
     */
    void populateObject(String line, InvocationProviders invocationProviders,
                        AeshContext aeshContext, boolean validate) throws CommandLineParserException, OptionValidatorException;

    /**
     * Returns a usage String based on the defined command and options.
     * Useful when printing "help" info etc.
     */
    String printHelp();

    /**
     * Parse a command line with the defined command as base of the rules.
     * If any options are found, but not defined in the command object an
     * CommandLineParserException will be thrown.
     * Also, if a required option is not found or options specified with value,
     * but is not given any value an OptionParserException will be thrown.
     *
     * The options found will be returned as a {@link CommandLine} object where
     * they can be queried after.
     *
     * @param line input
     * @return CommandLine
     */
    CommandLine<C> parse(String line);

    ProcessedOption lastParsedOption();

    /**
     * Parse a command line with the defined command as base of the rules.
     * If any options are found, but not defined in the command object an
     * CommandLineParserException will be thrown.
     * Also, if a required option is not found or options specified with value,
     * but is not given any value an CommandLineParserException will be thrown.
     *
     * The options found will be returned as a {@link CommandLine} object where
     * they can be queried after.
     *
     * @param line input
     * @param ignoreRequirements if we should ignore
     * @return CommandLine
     */
    CommandLine<C> parse(String line, boolean ignoreRequirements);

    CommandLine<C> parse(ParsedLineIterator iterator, boolean ignoreRequirements);

    void clear();

    boolean isGroupCommand();

    void setChild(boolean b);

    /**
     * Will return the correct command's parser. Used when a command have child commands and
     * we need to know which parser to use for completions.
     *
     * @return  correct parser
     */
    CommandLineParser<C> parsedCommand();
}
