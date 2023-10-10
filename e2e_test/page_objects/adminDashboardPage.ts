export function adminDashboardPage() {
  return {
    goToFailureReportList() {
      cy.findByText("Failure Reports").click()
      return {
        shouldContain(content: string) {
          cy.get("body").should("contain", content)
        },
      }
    },

    suggestedQuestionsForFineTuning() {
      cy.findByRole("button", { name: "Fine Tuning Data" }).click()
      return {
        expectComment(comment: string) {
          cy.findByText(comment)
        },
        downloadAIQuestionTrainingData() {
          cy.findByRole("button", { name: "Download All Examples" }).click()
          const downloadFilename = `${Cypress.config("downloadsFolder")}/fineTuningData.jsonl`

          return {
            expectNumberOfRecords(count: number) {
              cy.readFile(downloadFilename)
                .then((content) => (content.match(/messages/g) || []).length)
                .should("eq", count)
              return this
            },

            expectTxtInDownload(inputText: string) {
              cy.readFile(downloadFilename).should("contain", inputText)
            },
          }
        },

        updateQuestionSuggestionAndChoice(
          originalQuestionStem: string,
          newQuestion: Record<string, string>,
        ) {
          cy.findByText(originalQuestionStem).parent().dblclick()
          cy.formField("Stem").clear().type(newQuestion["Question Stem"])
          cy.get("#choice-0").clear().type(newQuestion["Choice A"])
          cy.findByRole("button", { name: "OK" }).click()
          cy.pageIsNotLoading()
          cy.findByText(newQuestion["Question Stem"])
        },
      }
    },
  }
}
