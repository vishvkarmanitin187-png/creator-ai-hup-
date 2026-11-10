const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// --- SECURITY BOUNDS TESTS ---

test("Unauthenticated user: cannot read projects", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("projects").get());
});

test("Authenticated user: cannot read another user's project", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("projects").doc("bob_proj").set({
      userId: BOB_UID,
      title: "Bob's AI Video",
      type: "video",
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("projects").doc("bob_proj").get());
});

test("Authenticated user: can create and read their own project", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("projects").doc("alice_proj").set({
      userId: ALICE_UID,
      title: "Alice's AI Reel",
      type: "reel",
      createdAt: new Date(),
    })
  );

  await assertSucceeds(aliceDb.collection("projects").doc("alice_proj").get());
});

test("Authenticated user: can query their own projects with matching filter", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("projects").doc("alice_proj_2").set({
      userId: ALICE_UID,
      title: "Alice Project 2",
      type: "script",
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("projects").where("userId", "==", ALICE_UID).get()
  );
});

test("Authenticated user: cannot query projects without userId filter", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("projects").get());
});
