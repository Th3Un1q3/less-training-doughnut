import mock_services from "./mock_services/index"

export const questionGenerationService = () => ({
  resetAndStubAskingMCQ: (record: Record<string, string>) => {
    const reply = JSON.stringify({
      stem: record["Question Stem"],
      correctChoiceIndex: 0,
      choices: [
        record["Correct Choice"],
        record["Incorrect Choice 1"],
        record["Incorrect Choice 2"],
      ],
    })
    cy.then(async () => {
      await mock_services.openAi().restartImposter()
      await mock_services
        .openAi()
        .chatCompletion()
        .requestMessageMatches({ role: "user", content: "Memory Assistant" })
        .stubFunctionCall("ask_single_answer_multiple_choice_question", reply)
    })
  },
  stubEvaluationQuestion: (record: Record<string, boolean | string>) => {
    cy.then(async () => {
      await mock_services
        .openAi()
        .chatCompletion()
        .requestMessageMatches({ role: "user", content: ".*critically check.*" })
        .stubFunctionCall("evaluate_question", JSON.stringify(record))
    })
  },
})
