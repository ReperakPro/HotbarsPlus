/*
 * Copyright (c) 2021 Video
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.videogamesm12.multihotbar;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import me.videogamesm12.multihotbar.commands.BackupCommand;
import me.videogamesm12.multihotbar.commands.NextCommand;
import me.videogamesm12.multihotbar.commands.PageCommand;
import me.videogamesm12.multihotbar.commands.PreviousCommand;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;

/**
 * CommandManager - Registers client-side commands in-game.
 * @author Video
 */
public class CommandManager
{
	public void register()
	{
		System.out.println("Registering commands");
		ClientCommandManager.DISPATCHER.register(
			ClientCommandManager.literal("hotbars+").then(
				ClientCommandManager.literal("backup").executes(new BackupCommand())
			).then(
				ClientCommandManager.literal("next").executes(new NextCommand())
			).then(
				ClientCommandManager.literal("previous").executes(new PreviousCommand())
			).then(
				ClientCommandManager.literal("goto").then(
					ClientCommandManager.argument("page", IntegerArgumentType.integer()).executes(new PageCommand())
				)
			)
		);
		System.out.println("Commands registered");
	}
}
