/* eslint-disable  @typescript-eslint/no-explicit-any */
/* eslint @typescript-eslint/no-unused-vars: 0 */
/// <reference types="cypress" />
// @ts-check
declare namespace Cypress {
  interface Chainable<Subject = any> {
    dismissLastErrorMessage(): Chainable<any>
    cleanDownloadFolder(): Chainable<any>
    clickButtonOnCardBody(noteTopic: any, buttonTitle: any): Chainable<any>
    clickLinkNob(target: string): Chainable<any>
    changeLinkType(targetNote: string, linkType: string): Chainable<any>
    clickRadioByLabel(labelText: any): Chainable<any>
    deleteNoteViaAPI(): Chainable<Subject>
    dialogDisappeared(): Chainable<any>
    expectBreadcrumb(item: string, addChildButton: boolean = true): Chainable<any>
    expectExactLinkTargets(targets: any): Chainable<any>
    expectFieldErrorMessage(field: string, message: string): Chainable<any>
    expectNoteCards(expectedCards: any): Chainable<any>
    findCardTitle(topic: string): Chainable<any>
    expectAMapTo(latitude: string, longitude: string): Chainable<any>
    findUserSettingsButton(userName: string): Chainable<any>
    failure(): Chainable<any>
    findNoteCardButton(noteTopic: string, btnTextOrTitle: string): Chainable<any>
    formField(label: string): Chainable<any>
    assignFieldValue(value: string): Chainable<any>
    fieldShouldHaveValue(value: string): Chainable<any>
    initialReviewInSequence(reviews: any): Chainable<any>
    initialReviewNotes(noteTopics: any): Chainable<any>
    initialReviewOneNoteIfThereIs({
      review_type,
      topic,
      additional_info,
      skip,
    }: any): Chainable<any>
    inPlaceEdit(noteAttributes: any): Chainable<any>
    loginAs(username: string): Chainable<any>
    logout(username?: string): Chainable<any>
    noteByTitle(noteTopic: string): Chainable<any>
    openSidebar(): Chainable<any>
    pageIsNotLoading(): Chainable<any>
    clearFocusedText(): Chainable<any>
    replaceFocusedTextAndEnter(test: any): Chainable<any>
    repeatReviewNotes(noteTopics: string): Chainable<any>
    goAndRepeatReviewNotes(noteTopics: string): Chainable<any>
    repeatMore(): Chainable<any>
    routerPush(fallback: any, name: any, params: any): Chainable<any>
    routerToReviews(): Chainable<any>
    routerToRoot(): Chainable<any>
    routerToInitialReview(): Chainable<any>
    routerToRepeatReview(): Chainable<any>
    searchNote(searchKey: any, options: any): Chainable<any>
    shouldSeeQuizWithOptions(questionParts: any, options: any): Chainable<any>
    startSearching(): Chainable<any>
    subscribeToNotebook(notebookTitle: string, dailyLearningCount: string): Chinputainable<any>
    undoLast(undoThpe: string): Chainable<any>
    unsubscribeFromNotebook(noteTopic: string): Chainable<any>
    yesIRemember(): Chainable<any>
  }
}
