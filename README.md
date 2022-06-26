# DiscordPlayerListWebhook
This is a simple Minecraft mod that uses the Discord Webhooks API to modify a message in a Discord channel when a player joins or leaves the server to display the current player list.

*Note: The mod works exclusively server-side.*

# Requirements
1. create Webhook in Discord server for the channel

2. create an initial message on the webhook, which will later be modified by the Minecraft mod.
See [Discord docs](https://discord.com/developers/docs/resources/webhook#execute-webhook) for how to create a webhook and message.

3. copy the message ID (see [Discord Docs](https://support.discord.com/hc/en-us/articles/206346498-Where-can-I-find-my-User-Server-Message-ID-) for how to copy a message ID).

4. Paste the Webhook-URL followed by "/messages/[message ID] into "[server root]/configs/DiscordPlayerListWebhook/cfg.config" AFTER the mod has been run.
