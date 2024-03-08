const { WebClient } = require('@slack/web-api');

const token = process.env.SLACK_BOT_TOKEN;
const channel = 'C03KMJQ53EK';
const prUrl = process.env.GITHUB_SERVER_URL + '/' + process.env.GITHUB_REPOSITORY + '/pull/' + process.env.GITHUB_EVENT_NUMBER;

const web = new WebClient(token);
console.log('Here is a consoles log');

// Check if the event is a pull request being opened
web.chat.postMessage({
channel: channel,
text: `New pull request opened: ${prUrl}`,
})
.then(response => {
console.log('Slack message sent:', response.ts);
console.log('::set-output name=slack-ts::' + response.ts);
})
.catch(error => {
console.error('Error sending Slack message:', error);
process.exit(1);
});

// Check if the event is a pull request being closed (merged or not)
if (process.env.GITHUB_EVENT_NAME === 'pull_request' && process.env.GITHUB_EVENT_ACTION === 'closed') {
  const threadTimestamp = process.env.SLACK_TS;
  web.chat.postMessage({
    channel: channel,
    text: `Pull request ${process.env.GITHUB_EVENT_ACTION === 'closed' ? 'closed' : 'merged'}: ${prUrl}`,
    thread_ts: threadTimestamp,
  })
  .then(response => {
    console.log('Slack reply sent:', response.ts);
  })
  .catch(error => {
    console.error('Error sending Slack reply:', error);
    process.exit(1);
  });
}
